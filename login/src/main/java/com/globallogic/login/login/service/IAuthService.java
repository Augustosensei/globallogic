package com.globallogic.login.login.service;

import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;

public interface IAuthService {
    
    LoginResponseDTO login(LoginRequestDTO request);
}
