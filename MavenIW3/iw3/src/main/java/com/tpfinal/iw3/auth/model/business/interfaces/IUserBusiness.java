package com.tpfinal.iw3.auth.model.business.interfaces;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.tpfinal.iw3.auth.model.User;
import com.tpfinal.iw3.auth.model.business.exception.BadPasswordException;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IUserBusiness {
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException;

	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException;
	
	public List<User> list() throws BusinessException;

}