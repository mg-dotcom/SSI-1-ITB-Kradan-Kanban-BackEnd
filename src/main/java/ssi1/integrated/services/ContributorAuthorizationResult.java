package ssi1.integrated.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContributorAuthorizationResult {
    private boolean canRead;
    private boolean canWrite;
}