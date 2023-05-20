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

/**
 * service class that conducts the business logic needed for tracking user labs
 */
@Service
public class LabSavedService {
    LabCanonicalService labCanonicalService;
    LabSavedRepository labSavedRepository;
    AuthService authService;
    @Autowired
    public LabSavedService(LabCanonicalService labCanonicalService,
                           LabSavedRepository labSavedRepository, AuthService authService){
        this.labCanonicalService = labCanonicalService;
        this.labSavedRepository = labSavedRepository;
        this.authService = authService;
    }

    /**
     * check if the user's already got an existing lab. if not (when the user starts a new lab for the first time),
     * start the process of copying from the canonical
     */
    public LabSaved getSavedLab(long pkey, String name) throws UnauthorizedException, LabZipException, IOException, InterruptedException, LabRetrievalException {
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
     * grab a lab from the canonical set and make it the user's saved lab
     */
    public LabSaved addNewSavedLab(long pkey, String name) throws LabZipException, IOException, InterruptedException, LabRetrievalException {
        LabCanonical labCanonical = labCanonicalService.getCanonicalLab(name);
        ProductKey productKey = authService.getProductKey(pkey);
        LabSaved labSaved = new LabSaved(labCanonical.getZip(), new Timestamp(System.currentTimeMillis()));
        labSaved.setProductKey(productKey);
        labSaved.setCanonical(labCanonical);
        return labSavedRepository.save(labSaved);
    }
    /**
     * provided the pkey and lab name, reset the user's lab to canonical
     * @return
     */
    public LabSaved resetLabProgress(long pkey, String name) throws LabZipException, LabRetrievalException, IOException, InterruptedException, UnauthorizedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        LabCanonical labCanonical = labCanonicalService.getCanonicalLab(name);
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        labSaved.setZip(labCanonical.getZip());
        labSavedRepository.save(labSaved);
        return labSaved;
    }
    /**
     * provided the zip file bytes of an existing lab, update the lab zip stored in the db with respoect
     * to the provided pkey
     * @return
     */
    public LabSaved saveLabProgress(long pkey, String name, byte[] zip) throws UnauthorizedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        labSaved.setZip(zip);
        labSavedRepository.save(labSaved);
        return labSaved;
    }

}
