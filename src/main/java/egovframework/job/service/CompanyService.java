package egovframework.job.service;

import egovframework.job.dto.CompanyDTO;

public interface CompanyService {

	// 회원가입 처리
	public void insertCompany(CompanyDTO companyDTO) throws Exception;

	// 아이디 중복 검색
	public boolean isIdDuplicate(String id) throws Exception;

	// 아이디 검색
	public CompanyDTO findById(String id) throws Exception;

	// 시퀀스 번호로 멤버 조회
	public CompanyDTO findByCNum(Long c_num) throws Exception;

	// 아이디 상세정보
	public CompanyDTO getCompanyDetail(String id) throws Exception;

	// 아이디 상세정보 수정
	public void updateCompanyDetail(CompanyDTO companyDTO) throws Exception;

	// 아이디 상세정보 수정
	public void updateSequenceCompanyDetail(CompanyDTO companyDTO) throws Exception;

	// 이전 비밀번호 찾기
	public String getPasswordByMNum(Long c_num) throws Exception;

	// 비밀번호 찾기
	public String findPassword(String c_id, String c_name, String phone) throws Exception;

	// 비밀번호 변경
	void updatePassword(CompanyDTO companyDTO) throws Exception;

	// 아이디 탈퇴
	public void deleteCompany(Long c_num) throws Exception;
}
