package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class UpdateUserSearchableStatusService {

    private final UserRepository userRepository;

    @Transactional
    public void doService(Long userId, boolean searchable) {
        User user = userRepository.findByIdOrElseThrow(userId);
        user.setSearchable(searchable);
    }
}
