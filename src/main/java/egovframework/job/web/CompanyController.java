package egovframework.job.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.job.dto.CompanyDTO;
import egovframework.job.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@Autowired
	EgovMessageSource egovMessageSource;

	@Autowired
	@Qualifier("companyPasswordEncoder")
	PasswordEncoder companyPasswordEncoder;

	// Constructor injection
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	// 회원가입 화면
	@RequestMapping(value = "signup", method = RequestMethod.GET)
	public String sugnUpView(@ModelAttribute("CompanyDTO") CompanyDTO companyDTO, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {
		return "/company/signup";
	}

	// 회원가입 처리
	@PostMapping("/signup")
	public ResponseEntity<?> actionSignUp(@RequestBody CompanyDTO companyDTO) {

		// 입력받은 비밀번호를 암호화하여 저장
		String encodedPassword = companyPasswordEncoder.encode(companyDTO.getC_password());
		companyDTO.setC_password(encodedPassword);

		try {
			// 회원 정보 저장
			companyService.registerCompany(companyDTO);
			return ResponseEntity.ok(companyDTO);
		} catch (Exception e) {
			String errorMessage = "회원가입 중 오류가 발생했습니다.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	// 로그아웃 처리
	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 세션 무효화
			request.getSession().invalidate();

			// 로그아웃 성공 메시지 반환
			String successMessage = "로그아웃이 성공적으로 처리되었습니다.";
			return ResponseEntity.ok(successMessage);
		} catch (Exception e) {
			// 로그아웃 실패 메시지 반환
			String errorMessage = "로그아웃 중 오류가 발생했습니다.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	// 상세정보 화면
	@GetMapping("/info/{id}")
	public ResponseEntity<?> companyInfo(@PathVariable String id, Authentication authentication) {
		try {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			CompanyDTO companyDetail = companyService.getCompanyDetail(id);

			if (companyDetail != null) {
				// Create a map to hold the member information
				Map<String, Object> response = new HashMap<>();
				response.put("id", userDetails.getUsername());
				response.put("company", companyDetail);

				return ResponseEntity.ok(response);
			} else {
				String errorMessage = "회원 정보를 찾을 수 없습니다.";
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
			}
		} catch (Exception e) {
			String errorMessage = "서버에서 오류가 발생했습니다.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	// 상세정보 수정 화면
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String companyUpdate(@PathVariable String id, Authentication authentication, Model model) {

		// 현재 로그인한 사용자의 정보를 얻어온다.
		try {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			CompanyDTO companyDetail = companyService.getCompanyDetail(id);

			model.addAttribute("id", userDetails.getUsername());
			model.addAttribute("company", companyDetail);
			return "/company/update";
		} catch (Exception e) {
			// Handle the exception or show an error page
			return "error";
		}
	}

	// 상세정보 수정 처리
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public String updateCompanyDetail(@PathVariable String id, @ModelAttribute("company") CompanyDTO companyDTO) {
		try {
			companyDTO.setC_id(id);
			companyService.updateCompanyDetail(companyDTO);
			return "redirect:/company/info.do/" + id;
		} catch (Exception e) {
			// Handle the exception or show an error page
			return "error";
		}
	}

	// 기업 탈퇴
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteCompany(Authentication authentication) {
		try {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String id = userDetails.getUsername();

			companyService.deleteCompany(id);

			return "redirect:/company/login.do";
		} catch (Exception e) {
			// Handle the exception or show an error page
			return "error";
		}
	}
}
