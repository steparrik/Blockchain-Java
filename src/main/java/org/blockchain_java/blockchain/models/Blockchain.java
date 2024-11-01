package org.blockchain_java.blockchain.models;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ToString
public class Blockchain {
    @Value("${hash.difficulty_prefix}")
    private String difficultyPrefix;

    private List<Block> chain = new ArrayList<>();
}
