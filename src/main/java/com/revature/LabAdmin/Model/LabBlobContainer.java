package com.revature.LabAdmin.Model;

import lombok.*;
import org.springframework.data.annotation.Transient;

/**
 * Transient data type used for the transfer of the ZIPBLOB over the web.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class LabBlobContainer {
    private byte[] bytes;
}
