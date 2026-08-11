package com.manada.backend.modules.users;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.common.security.JwtService;
import com.manada.backend.modules.foundations.FoundationProfile;
import com.manada.backend.modules.foundations.FoundationProfileRepository;
import com.manada.backend.modules.providers.ProviderProfile;
import com.manada.backend.modules.providers.ProviderProfileRepository;
import com.manada.backend.modules.providers.ProviderType;
import com.manada.backend.modules.users.dto.AuthResponse;
import com.manada.backend.modules.users.dto.LoginRequest;
import com.manada.backend.modules.users.dto.RegisterRequest;
import com.manada.backend.modules.users.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FoundationProfileRepository foundationProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            FoundationProfileRepository foundationProfileRepository,
            ProviderProfileRepository providerProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.foundationProfileRepository = foundationProfileRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.accountType() == AccountType.FUNDACION && req.foundation() == null) {
            throw ApiException.badRequest("Faltan los datos de la fundación (orgName, city).");
        }
        if (req.accountType() == AccountType.PROVEEDOR && req.provider() == null) {
            throw ApiException.badRequest("Faltan los datos del proveedor (providerType, businessName, city).");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw ApiException.conflict("Ese correo ya está registrado.");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setPhone(req.phone());
        user.setAccountType(req.accountType());
        user = userRepository.save(user);

        if (req.accountType() == AccountType.FUNDACION) {
            FoundationProfile fp = new FoundationProfile();
            fp.setUserId(user.getId());
            fp.setOrgName(req.foundation().orgName());
            fp.setTaxId(req.foundation().taxId());
            fp.setCity(req.foundation().city());
            foundationProfileRepository.save(fp);
        }

        if (req.accountType() == AccountType.PROVEEDOR) {
            ProviderProfile pp = new ProviderProfile();
            pp.setUserId(user.getId());
            try {
                pp.setProviderType(ProviderType.valueOf(req.provider().providerType()));
            } catch (IllegalArgumentException ex) {
                throw ApiException.badRequest("providerType inválido. Debe ser uno de: TIENDA, VETERINARIA, PASEADOR, PELUQUERIA, GUARDERIA, ADIESTRADOR.");
            }
            pp.setBusinessName(req.provider().businessName());
            pp.setTaxId(req.provider().taxId());
            pp.setCity(req.provider().city());
            providerProfileRepository.save(pp);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getAccountType().name());
        return new AuthResponse(token, UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getAccountType().name());
        return new AuthResponse(token, UserResponse.from(user));
    }

    public UserResponse me(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        return UserResponse.from(user);
    }
}
