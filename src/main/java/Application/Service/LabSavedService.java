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
 * service class that conducts the business logic needed for tracking user labs
 */
@Service
public class LabSavedService {
    LabCanonicalService labCanonicalService;
    LabSavedRepository labSavedRepository;
    AuthService authService;
    BlobService blobService;
    @Autowired
    public LabSavedService(LabCanonicalService labCanonicalService,
                           LabSavedRepository labSavedRepository, AuthService authService, BlobService blobService){
        this.labCanonicalService = labCanonicalService;
        this.labSavedRepository = labSavedRepository;
        this.authService = authService;
        this.blobService = blobService;
    }

    /**
     * check if the user's already got an existing lab. if not (when the user starts a new lab for the first time),
     * start the process of copying from the canonical
     */
    public ByteArrayResource getSavedLab(long pkey, String name) throws UnauthorizedException, LabZipException, IOException, InterruptedException, LabRetrievalException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        if (labSaved == null) {
            addNewSavedLab(pkey, name);
            ByteArrayResource byteArray = blobService.getSavedFromAzure(pkey, name);
            return byteArray;
        }else{
//        REMOVE THIS LATER!!!
//        since there is no save functionality yet anyways, just reset the lab every time its requested
            resetLabProgress(pkey, name);
            ByteArrayResource byteArray = blobService.getSavedFromAzure(pkey, name);
            return byteArray;
        }
    }

    /**
     * grab a lab from the canonical set and make it the user's saved lab
     */
    public ByteArrayResource addNewSavedLab(long pkey, String name) throws LabZipException, LabRetrievalException, IOException, InterruptedException {
        labCanonicalService.getCanonicalLab(name);
        ProductKey productKey = authService.getProductKey(pkey);
        LabSaved labSaved = new LabSaved(new Timestamp(System.currentTimeMillis()));
        labSaved.setProductKey(productKey);
        labSaved.setName(name);
        labSavedRepository.save(labSaved);
        byte[] canonicalLabBytes = blobService.getCanonicalFromAzure(name).getByteArray();
        blobService.saveSavedBlob(pkey, name, canonicalLabBytes);
        return new ByteArrayResource(canonicalLabBytes);
    }
    /**
     * provided the pkey and lab name, reset the user's lab to canonical
     * @return
     */
    public LabSaved resetLabProgress(long pkey, String name) throws LabZipException, LabRetrievalException, IOException, InterruptedException, UnauthorizedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        byte[] labBytes = blobService.getCanonicalFromAzure(name).getByteArray();
        blobService.saveSavedBlob(pkey, name, labBytes);
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        labSaved.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        labSavedRepository.save(labSaved);
        return labSaved;
    }
    /**
     * provided the zip file bytes of an existing lab, update the lab zip stored in the db with respoect
     * to the provided pkey
     * @return
     */
    public LabSaved saveLabProgress(long pkey, String name, byte[] labBytes) throws UnauthorizedException {
        if(!authService.validateUser(pkey)){
            throw new UnauthorizedException();
        }
        blobService.saveSavedBlob(pkey, name, labBytes);
        LabSaved labSaved = labSavedRepository.getSpecificSavedLab(pkey, name);
        labSaved.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        labSavedRepository.save(labSaved);
        return labSaved;
    }
}
