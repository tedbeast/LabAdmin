package Application.Service;

import java.io.File;
import com.azure.identity.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlobService {
    DefaultAzureCredential defaultCredential;
    BlobServiceClient blobServiceClient;
    BlobContainerClient canonicals;
    BlobContainerClient saved;
    @Autowired
    public BlobService(){
        // Retrieve the connection string for use with the application.
        String connectStr = "";

        // Create a BlobServiceClient object using a connection string
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectStr)
                .buildClient();

        canonicals = blobServiceClient.getBlobContainerClient("canonicals");
        saved = blobServiceClient.getBlobContainerClient("saved");
    }
    public String saveCanonicalBlob(String name, File zip){
        String blobName = (name).toLowerCase().trim();
        BlobClient blobClient = canonicals.getBlobClient(blobName);
        blobClient.uploadFromFile(zip.getPath());
        return blobName;
    }
}
