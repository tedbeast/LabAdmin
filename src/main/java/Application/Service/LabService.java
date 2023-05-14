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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LabService {
    @Autowired
    CMDService cmdService;
    @Autowired
    LabCanonicalRepository labCanonicalRepository;
    @Autowired
    LabSavedRepository labSavedRepository;
    @Autowired
    AuthService authService;
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
    public LabCanonical addNewCanonicalLab(String name) throws LabZipException, IOException, InterruptedException {
        File zipfile = generateCanonicalLabZip(name);
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        LabCanonical labCanonical = new LabCanonical(name, zipBytes, new Timestamp(System.currentTimeMillis()));
        zipfile.delete();
        return labCanonicalRepository.save(labCanonical);
    }
    public LabCanonical updateExistingCanonicalLabZip(String name) throws LabZipException, IOException, InterruptedException {
        File zipfile = generateCanonicalLabZip(name);
        LabCanonical labCanonical = labCanonicalRepository.findByName(name);
        byte[] zipBytes = Files.readAllBytes(Path.of(zipfile.getPath()));
        labCanonical.setZip(zipBytes);
        labCanonical.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        zipfile.delete();
        return labCanonicalRepository.save(labCanonical);
    }
    public boolean checkForCanonicalLabExistenceRepo(){
        return true;
    }
    public boolean checkForCanonicalLabUpdate(){
        return false;
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
    public LabSaved updateExistingSavedLabZip(){
        return null;
    }
    public File generateCanonicalLabZip(String name) throws IOException, InterruptedException, LabZipException {
        if(name == null || name.length()<1){
            throw new LabZipException();
        }
        cmdService.runCommandReturnOutput("git clone https://github.com/tedbeast/"+name);
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
