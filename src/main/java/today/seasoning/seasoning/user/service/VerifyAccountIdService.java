package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class VerifyAccountIdService {

    private final UserRepository userRepository;

    public boolean verify(AccountId accountId) {
        return !userRepository.existsByAccountId(accountId.get());
    }
}
