package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class VerifyAccountIdService {

    private final UserRepository userRepository;

    public void verify(AccountId accountId) {
        if(userRepository.existsByAccountId(accountId.get())) {
            throw new CustomException(HttpStatus.CONFLICT, "아이디 중복");
        }
    }
}
