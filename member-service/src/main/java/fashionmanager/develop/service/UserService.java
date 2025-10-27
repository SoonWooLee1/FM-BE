package fashionmanager.develop.service;

import fashionmanager.develop.dto.AssignedRightDTO;
import fashionmanager.develop.entity.AssignedRight;
import fashionmanager.develop.mapper.UserMapper;
import fashionmanager.develop.dto.UserDTO;
import fashionmanager.develop.entity.User;
import fashionmanager.develop.exception.DuplicateUserException;
import fashionmanager.develop.exception.NotFoundException;
import fashionmanager.develop.exception.ValidationException;
import fashionmanager.develop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    /* 설명. ModelMapper 안쓰고 수동으로 매핑하는 법 */
    UserDTO userToUserDTO(User user) {


        return modelMapper.map(user, UserDTO.class);
    }






    // 1. 회원가입 기능
    @Transactional
    public UserDTO registUser(UserDTO newUser) {


        // ===== 값 존재 여부 체크 =====
        if (newUser.getUserId() == null || newUser.getUserId().trim().isEmpty()) {
            throw new ValidationException("아이디는 필수 입력 값입니다.");
        }
        if (newUser.getUserEmail() == null || newUser.getUserEmail().trim().isEmpty()) {
            throw new ValidationException("이메일은 필수 입력 값입니다.");
        }

        if (newUser.getUserPwd() == null || newUser.getUserPwd().trim().isEmpty()) {
            throw new ValidationException("비밀번호는 필수 입력 값입니다.");
        }
        if (newUser.getUserName() == null || newUser.getUserName().trim().isEmpty()) {
            throw new ValidationException("이름은 필수 입력 값입니다.");
        }

        if (newUser.getUserAge() <= 0) {
            throw new ValidationException("나이는 1 이상이어야 합니다.");
        }
        if (newUser.getUserGender() == null || newUser.getUserGender().trim().isEmpty()) {
            throw new ValidationException("성별은 필수 입력 값입니다.");
        }

        // ===== 중복 검사 =====
        if (userRepository.existsByUserId(newUser.getUserId())) {
            throw new DuplicateUserException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByUserEmail(newUser.getUserEmail())) {
            throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
        }


        // 비밀번호 해싱 (평문 → BCrypt 다이제스트)
        newUser.setUserPwd(bCryptPasswordEncoder.encode(newUser.getUserPwd()));


        // DTO -> Entity 변환
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User user = modelMapper.map(newUser, User.class);

        // DB 저장
        User savedUser = userRepository.save(user);

        // Entity -> DTO 변환 후 반환
        return modelMapper.map(savedUser, UserDTO.class);


    }

    // 3. 회원 번호를 불러와 특정 회원을 조회하는 기능(JPA)

    public UserDTO findUserById ( int userNum){

        User user = userRepository.findById(userNum)
                .orElseThrow(IllegalArgumentException::new);
        return userToUserDTO(user);

    }



    // 4. 회원 정보를 수정하는 기능

    @Transactional
    public UserDTO modifyMenu(int userNum, Map<String, Object> updates) {
        User foundUser = userRepository.findById(userNum)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        // ===== 아이디 중복 검사 (본인 제외) =====
        if (updates.containsKey("userId")) {
            String newUserId = String.valueOf(updates.get("userId"));
            if (!foundUser.getUserId().equals(newUserId) &&
                    userRepository.existsByUserId(newUserId)) {
                throw new DuplicateUserException("이미 사용 중인 아이디입니다.");
            }
            foundUser.setUserId(newUserId);
        }

        // ===== 이메일 중복 검사 (본인 제외) =====
        if (updates.containsKey("userEmail")) {
            String newEmail = String.valueOf(updates.get("userEmail"));
            if (!foundUser.getUserEmail().equals(newEmail) &&
                    userRepository.existsByUserEmail(newEmail)) {
                throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
            }
            foundUser.setUserEmail(newEmail);
        }

        // 회원 정보 수정(비밀번호 제외)
        if (updates.containsKey("userName")) {
            foundUser.setUserName(String.valueOf(updates.get("userName")));
        }
        if (updates.containsKey("userAge")) {
            Object ageObj = updates.get("userAge");
            int age = (ageObj instanceof Number)
                    ? ((Number) ageObj).intValue()
                    : Integer.parseInt(String.valueOf(ageObj));
            foundUser.setUserAge(age);
        }
        if (updates.containsKey("userGender")) {
            foundUser.setUserGender(String.valueOf(updates.get("userGender")));
        }



        // 비밀번호 보안부분 -> 중복 방지(새 비밀번호 == 기존 비밀번호인지 확인)
        if (updates.containsKey("userPwd")) {
            String newPwd = String.valueOf(updates.get("userPwd"));

            if (newPwd == null || newPwd.trim().isEmpty()) {
                throw new ValidationException("새 비밀번호는 비울 수 없습니다.");
            }

            // DB 해시된 비밀번호와 비교 (같으면 중복)
            if (bCryptPasswordEncoder.matches(newPwd, foundUser.getUserPwd())) {
                throw new ValidationException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
            }

            // 중복 아니면 인코딩 후 저장
            foundUser.setUserPwd(bCryptPasswordEncoder.encode(newPwd));
        }

        User saved = userRepository.save(foundUser);

        // 응답에 비밀번호 포함하지 않기!
        UserDTO dto = modelMapper.map(saved, UserDTO.class);
        dto.setUserPwd(null); // 여기서 null 값 넣어줘서 확인하지 못 하게 하기
        return dto;
    }



        // 5. 메시지 수신여부를 수정하는 기능

        @Transactional
        public UserDTO modifyMessage ( int userNum, Map<String, Object > updates){
            User foundUser = userRepository.findById(userNum)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

            if (updates.containsKey("userMessageAllow")) {
                foundUser.setUserMessageAllow((Integer) updates.get("userMessageAllow"));
            }

            User savedUser = userRepository.save(foundUser);
            return modelMapper.map(savedUser, UserDTO.class);
        }


        // 6. 신고 누적 or 하루 신고 가능 횟수 수정(관리자 권한)

        @Transactional
        public UserDTO modifyReport ( int userNum, Map<String, Object > updates){
            User foundUser = userRepository.findById(userNum)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

            if (updates.containsKey("reportCount")) {
                foundUser.setReportCount((Integer) updates.get("reportCount"));
            }
            if (updates.containsKey("dailyReportCount")) {
                foundUser.setDailyReportCount((Integer) updates.get("dailyReportCount"));
            }

            User savedUser = userRepository.save(foundUser);
            return modelMapper.map(savedUser, UserDTO.class);
        }


        // 7. 회원 정보를 삭제하는 기능

        @Transactional
        public void userDelete ( int userNum){
            if (!userRepository.existsById(userNum)) {
                throw new NotFoundException("존재하지 않는 회원입니다.");
            }
            userRepository.deleteById(userNum);
        }


//    // 2. 회원 전체를 조회하는 기능(JPA)
//
//    public Page<UserDTO> findUserList(Pageable pageable) {
//
//        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1,
//                pageable.getPageSize(),
//                Sort.by("userNum").descending());
//        Page<User> userList = userRepository.findAll(pageable);
//
//        /* 설명. Page는 자체로 stream이다. */
//        return userList.map(menu -> ParkmodelMapper.map(menu, UserDTO.class));
//
//    }


//    // 4. 회원 정보를 수정하는 기능(html)
//
//    @Transactional
//    public void modifyMenu(UserDTO modifyMenu) {
//
//        User foundUser = userRepository.findById(modifyMenu.getUserNum()).get();
//
//
//        // 아이디 중복 검사 (본인 제외)
//        if (!foundUser.getUserId().equals(modifyMenu.getUserId()) &&
//                userRepository.existsByUserId(modifyMenu.getUserId())) {
//            throw new RuntimeException("이미 사용 중인 아이디입니다.");
//        }
//
//        // 이메일 중복 검사 (본인 제외)
//        if (!foundUser.getUserEmail().equals(modifyMenu.getUserEmail()) &&
//                userRepository.existsByUserEmail(modifyMenu.getUserEmail())) {
//            throw new RuntimeException("이미 사용 중인 이메일입니다.");
//        }
//
//
//        foundUser.setUserId(modifyMenu.getUserId());
//        foundUser.setUserPwd(modifyMenu.getUserPwd());
//
//        foundUser.setUserEmail(modifyMenu.getUserEmail());
//
//
//
//        foundUser.setUserName(modifyMenu.getUserName());
//
//        foundUser.setUserAge(modifyMenu.getUserAge());
//
//        foundUser.setUserGender(modifyMenu.getUserGender());
//
//
//
//    }


}
