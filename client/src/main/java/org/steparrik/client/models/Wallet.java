package org.steparrik.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Wallet {
    private String address;
    private String publicKey;
    private String privateKey;
}
