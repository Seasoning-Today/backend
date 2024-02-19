package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class FindUserSearchableStatusService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean doService(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        return user.isSearchable();
    }
}
