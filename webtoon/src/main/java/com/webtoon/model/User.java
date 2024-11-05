package com.webtoon.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must be less than 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Nickname is required")
    @Size(max = 20, message = "Nickname must be less than 20 characters")
    @Column(nullable = false, length = 20)
    private String nickname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false, length = 255)
    private String password;

    @NotNull(message = "Birthdate is required")
    @Column(nullable = false)
    private LocalDate birthdate;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, length = 10)
    // ROOT 최고 권한자 계정 설정 필요 (우선순위 1)
    // ROOT 계정에서 개발자 채용 허가 명령이 내려지면 관리자 권한 부여해서 ADMIN 전환 (우선순위 2)
    // ROOT 계정에서 원고 검토 후 웹툰 작가 권한 부여 명령이 내려지면 개발자 페이지에서 ARTIST로 전환 (우선순위 3) 
    // 멤버쉽 가입 결제 시 USER => MEMBER
    // USER가 기본값
    private String role = "USER";
    
    private String phoneNumber;

    // 이용 약관 동의 : 사용자 가입 시 서비스 이용 약관에 동의했는지 여부를 저장.
    @Column(nullable = false)
    private boolean termsAgreed;

    // 마케팅 이메일 동의 : 사용자가 마케팅 또는 광고 이메일을 수신하는 데 동의했는지 여부를 저장.
    private boolean marketingConsent;
    
    // 계정 상태 : 계정이 활성화되었는지, 정지되었는지 등을 기록하는 컬럼. 관리자가 계정을 제어하는 데 유용.
    // ACTIVE = 계정 활성화 상태
    // SUSPENDED = 계정이 일시적으로 정지된 상태 (비밀번호 5회 이상 틀림 => 본인 확인 후 비밀번호 재설정 후 ACTIVE로 전환)
    // DEACTIVATED = 계정이 영구적으로 비활성화 (계정 비활성화 신청 시)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    // 마지막 로그인 시간 : 사용자가 마지막으로 로그인한 시간을 기록.
    private LocalDateTime lastLogin;

    // 계정 생성 시간 : 회원 가입하여 계정을 생성한 시간
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 계정 갱신 시간 : 계정 정보를 업데이트한 시간    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
    	LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;  // 회원가입 시 updatedAt도 createdAt과 동일하게 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum AccountStatus {
        ACTIVE, SUSPENDED, DEACTIVATED
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isTermsAgreed() {
		return termsAgreed;
	}

	public void setTermsAgreed(boolean termsAgreed) {
		this.termsAgreed = termsAgreed;
	}

	public boolean isMarketingConsent() {
		return marketingConsent;
	}

	public void setMarketingConsent(boolean marketingConsent) {
		this.marketingConsent = marketingConsent;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
