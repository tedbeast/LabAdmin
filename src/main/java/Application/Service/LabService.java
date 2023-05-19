package Application.Service;

import Application.Exception.LabRetrievalException;
import Application.Exception.LabZipException;
import Application.Exception.UnauthorizedException;
import Application.Model.LabCanonical;
import Application.Model.LabSaved;
import Application.Model.ProductKey;
import Application.Repository.LabCanonicalRepository;
import Application.Repository.LabSavedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LabService {
    CMDService cmdService;
    LabCanonicalRepository labCanonicalRepository;
    LabSavedRepository labSavedRepository;
    AuthService authService;
    @Autowired
    public LabService(CMDService cmdService, LabCanonicalRepository labCanonicalRepository,
                      LabSavedRepository labSavedRepository, AuthService authService){
        this.cmdService = cmdService;
        this.labCanonicalRepository = labCanonicalRepository;
        this.labSavedRepository = labSavedRepository;
        this.authService = authService;
    }
    public LabCanonical getCanonicalLab(long pkey, String name) throws UnauthorizedException, LabRetrievalException, LabZipException, IOException, InterruptedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        LabCanonical lab = labCanonicalRepository.findByName(name);
        if(lab == null){
            if(checkForCanonicalLabExistenceRepo()){
                lab = addNewCanonicalLab(name);
                return lab;
            }else{
                throw new LabRetrievalException();
            }
        }else{
            if(checkForCanonicalLabUpdate()) {
                lab = updateExistingCanonicalLabZip(name);
                return lab;
            }else{
                return lab;
            }
        }
    }
    public LabSaved getSavedLab(long pkey, String name) throws UnauthorizedException, LabZipException, IOException, InterruptedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        if (labSaved == null) {
            return addNewSavedLab(pkey, name);
        }else{
            return labSaved;
        }
    }

    /**
     * ungodly exception handling. dont look i had 30 minutes to fix this before my flight
     * @param name
     * @return
     * @throws LabZipException
     * @throws IOException
     * @throws InterruptedException
     */
    public LabCanonical addNewCanonicalLab(String name) throws LabZipException, IOException, InterruptedException {

        File zipfile = null;
        try{
            zipfile = generateCanonicalLabZip("https://github.com/exa-coding-labs/",name);
        }catch (IOException e){
            try{
                zipfile = generateCanonicalLabZip("https://github.com/peplabs/",name);
            }catch (IOException e2){
                try{
                    zipfile = generateCanonicalLabZip("https://github.com/tedbeast/",name);
                }catch (IOException e3){
                    zipfile = new File(name+".zip");
                    zipfile.delete();
                    throw new RuntimeException("gave up trying to clone the lab dont make a zipfile!");
                }

            }
        }
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        LabCanonical labCanonical = new LabCanonical(name, zipBytes, new Timestamp(System.currentTimeMillis()));
        return labCanonicalRepository.save(labCanonical);
    }

    /**
     * i repeated myself because i'm a BAD coder and now my flight's in 15 minutes!
     * @param name
     * @return
     * @throws LabZipException
     * @throws IOException
     * @throws InterruptedException
     */
    public LabCanonical updateExistingCanonicalLabZip(String name) throws LabZipException, IOException, InterruptedException {
        File zipfile = null;
        try{
            zipfile = generateCanonicalLabZip("https://github.com/exa-coding-labs/",name);
        }catch (IOException e){
            try{
                zipfile = generateCanonicalLabZip("https://github.com/peplabs/",name);
            }catch (IOException e2){
                try{
                    zipfile = generateCanonicalLabZip("https://github.com/tedbeast/",name);
                }catch (IOException e3){
                    zipfile = new File(name+".zip");
                    zipfile.delete();
                    throw new RuntimeException("gave up trying to clone the lab dont make a zipfile!");
                }

            }
        }
        LabCanonical labCanonical = labCanonicalRepository.findByName(name);
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        labCanonical.setZip(zipBytes);
        labCanonical.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        zipfile.delete();
        return labCanonicalRepository.save(labCanonical);
    }

    /**
     * TODO check for the existence of the repo
     * @return
     */
    public boolean checkForCanonicalLabExistenceRepo(){
        return true;
    }
    /**
     * TODO
     * for now, just update the lab every dang time.
     * @return
     */
    public boolean checkForCanonicalLabUpdate(){
        return true;
    }
    public LabSaved addNewSavedLab(long pkey, String name) throws LabZipException, IOException, InterruptedException {
        LabCanonical labCanonical = labCanonicalRepository.findByName(name);
        if(labCanonical == null){
            labCanonical = addNewCanonicalLab(name);
        }
        ProductKey productKey = authService.getProductKey(pkey);
        LabSaved labSaved = new LabSaved(labCanonical.getZip(), new Timestamp(System.currentTimeMillis()));
        labSaved.setProductKey(productKey);
        labSaved.setCanonical(labCanonical);
        return labSavedRepository.save(labSaved);
    }

    /**
     * TODO: provided the pkey
     * @return
     */
    public LabSaved resetLabProgress(long pkey, String name){
        return null;
    }
    /**
     * TODO: provided the zip file bytes of an existing lab, update the lab zip stored in the db with respoect
     * to the provided pkey
     * @return
     */
    public LabSaved saveLabProgress(long pkey, String name, byte[] zip){
        return null;
    }
    public File generateCanonicalLabZip(String ghorgPrefix, String name) throws IOException, InterruptedException, LabZipException {
        if(name == null || name.length()<1){
            throw new LabZipException();
        }
        cmdService.runCommandReturnOutput("git clone "+ghorgPrefix+name);
        pack("./"+name, "./"+name+".zip");
        File dir = new File("./"+name);
        deleteDirectory(dir);
        File zip = new File("./"+name+".zip");
        return zip;
    }
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
     * not sure if it's possible to zip files without creating them
     * might be cool
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
