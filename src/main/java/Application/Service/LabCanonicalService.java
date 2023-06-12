package Application.Service;

import Application.App;
import Application.Exception.LabRetrievalException;
import Application.Exception.LabZipException;
import Application.Exception.UnauthorizedException;
import Application.Model.LabCanonical;
import Application.Repository.LabCanonicalRepository;
import Application.Util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * class that conducts the interaction between git repository based labs and their zip stored counterpart
 */
@Service
public class LabCanonicalService {
    CMDService cmdService;
    LabCanonicalRepository labCanonicalRepository;
    BlobService blobService;
    @Autowired
    public LabCanonicalService(CMDService cmdService, LabCanonicalRepository labCanonicalRepository, BlobService blobService){
        this.cmdService = cmdService;
        this.labCanonicalRepository = labCanonicalRepository;
        this.blobService = blobService;
    }


    public ByteArrayResource getCanonicalByteArray(String name) throws LabZipException, LabRetrievalException, IOException, InterruptedException {
        LabCanonical labCanonical = getCanonicalLab(name);
        return blobService.getCanonicalFromAzure(labCanonical.getName());
    }
    /**
     * check for brand new labs or lab updates prior to returning the canonical
     * @param name
     * @return
     * @throws LabRetrievalException
     * @throws LabZipException
     * @throws IOException
     * @throws InterruptedException
     */
    public LabCanonical getCanonicalLab(String name) throws LabRetrievalException, LabZipException, IOException, InterruptedException {
        if(name == null || name.length() < 2){
            App.log.info("malformed lab name: "+name);
            throw new LabRetrievalException();
        }
        LabCanonical lab = labCanonicalRepository.findByName(name);
        String[] tuple = getLatestCanonicalCommit(name);
        if(tuple == null){
            App.log.info("no such lab found: "+name);
            throw new LabRetrievalException();
        }
        String commit = tuple[0];
        String source = WebUtil.baseUrl[Integer.parseInt(tuple[1])];
        if(lab == null){
            if(commit!=null){
                App.log.info("adding as a new canonical: "+name+", "+commit+", "+source);
                lab = addNewCanonicalLab(name, commit, source);
                return lab;
            }else{
                App.log.warn("commit hash null or malformed: "+name+", "+commit+", "+source);
                throw new LabRetrievalException();
            }
        }else{
            if(checkForCanonicalLabUpdate(lab, commit)) {
                App.log.info("updating canonical: "+name+", "+commit+", "+source);
                lab = updateExistingCanonicalLabZip(name, commit, source);
                return lab;
            }else{
                App.log.info("loading canonical: "+name+", "+commit+"/"+lab.getCommitHash()+", "+source);
                return lab;
            }
        }
    }

    /**
     * take the git repo from the web, if there are no issues create the zip
     * @param name
     * @param source
     * @return
     * @throws InterruptedException
     * @throws LabZipException
     */
    public File getZipFromWeb(String name, String source) throws InterruptedException, LabZipException {
        File zipfile;
        try{
            zipfile = generateCanonicalLabZip(source, name);
        }catch (LabZipException e){
//            ensure that no artifacts remain when an issue occurs
            zipfile = new File(name+".zip");
            zipfile.delete();
            App.log.warn("an issue occurred while creating the zipfile: "+name+", "+source);
            throw new LabZipException();
        }catch (IOException e){
            zipfile = new File(name+".zip");
            zipfile.delete();
            App.log.warn("an unspecified IOException occurred while creating the zipfile: "+name+", "+source);
            throw new LabZipException();
        }
        return zipfile;
    }

    /**
     * process for saving the new lab canonical entity, including transferring the zip to blob
     * @param name
     * @param commit
     * @param source
     * @return
     * @throws LabZipException
     * @throws IOException
     * @throws InterruptedException
     */
    public LabCanonical addNewCanonicalLab(String name, String commit, String source) throws LabZipException, IOException, InterruptedException {
        File zipfile = getZipFromWeb(name, source);
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        LabCanonical labCanonical = new LabCanonical(name, commit, source);
        blobService.saveCanonicalBlob(name, zipfile);
        zipfile.delete();
        return labCanonicalRepository.save(labCanonical);
    }

    /**
     * process for updating a lab zip
     * @param name
     * @param commit
     * @param source
     * @return
     * @throws LabZipException
     * @throws IOException
     * @throws InterruptedException
     */
    public LabCanonical updateExistingCanonicalLabZip(String name, String commit, String source) throws LabZipException, IOException, InterruptedException {
        File zipfile = getZipFromWeb(name, source);
        LabCanonical labCanonical = labCanonicalRepository.findByName(name);
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        labCanonical.setCommitHash(commit);
        blobService.saveCanonicalBlob(name, zipfile);
        zipfile.delete();
        return labCanonicalRepository.save(labCanonical);
    }
    /**
     * check for the existence of the repo by polling all github repo urls
     * @return
     */
    public String[] getLatestCanonicalCommit(String name){
        for(int i = 0; i < WebUtil.baseUrl.length; i++){
            String commit = checkCanonicalLabSource(WebUtil.baseUrl[i]+name);
            if(commit!=null){
                return new String[]{commit, ""+i};
            }
        }
        return null;
    }
    /**
     * check for the existence of the repo by polling single github repo url
     * @return
     */
    public String checkCanonicalLabSource(String url){
        try{
            String output = cmdService.runCommandReturnOutput("git ls-remote "+url);
            if(output.length()>1){
                return output.substring(0, 40);
            }
        }catch(Exception e){
            return null;
        }
        return null;
    }
    /**
     * check for updates to the lab by comparing the commit hashes
     * @return
     */
    public boolean checkForCanonicalLabUpdate(LabCanonical labCanonical, String commit){
        if(labCanonical.getCommitHash().equals(commit)==false){
            return true;
        }else{
            return false;
        }
    }

    /**
     * clone git repo, convert to zip.
     */
    public File generateCanonicalLabZip(String ghorgPrefix, String name) throws IOException, InterruptedException, LabZipException {
        cmdService.runCommandReturnOutput("git clone "+ghorgPrefix+name);
        pack("./"+name, "./"+name+".zip");
        File dir = new File("./"+name);
        deleteDirectory(dir);
        File zip = new File("./"+name+".zip");
        return zip;
    }
    /**
     * git repo directory: "i dont wanna play with you any more"
     * @param directoryToBeDeleted
     * @return
     */
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    /**
     * package the git repo directory to zip
     * @param sourceDirPath
     * @param zipFilePath
     * @throws IOException
     */
    public void pack(String sourceDirPath, String zipFilePath) throws IOException {
        File zip = new File(zipFilePath);
        zip.delete();
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }
}
