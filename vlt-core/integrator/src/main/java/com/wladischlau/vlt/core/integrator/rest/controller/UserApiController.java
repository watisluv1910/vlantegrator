package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.UserApi;
import com.wladischlau.vlt.core.integrator.rest.dto.UserSettingsDto;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserApiController extends ApiController implements UserApi {

    private final VltDataService vltDataService;

    public UserApiController(DtoMapper dtoMapper, VltDataService vltDataService) {
        super(dtoMapper);
        this.vltDataService = vltDataService;
    }

    @Override
    public ResponseEntity<UserSettingsDto> getUserSettings(JwtAuthenticationToken principal) {
        return logRequestProcessing(GET_USER_SETTINGS, () -> {
            var username = principal.getName();
            var settings = vltDataService.findUserSettings(username);
            var dto = dtoMapper.toDto(settings);
            return ResponseEntity.ok(dto);
        });
    }

    @Override
    public ResponseEntity<Void> updateUserSettings(UserSettingsDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(UPDATE_USER_SETTINGS, () -> {
            var username = principal.getName();
            vltDataService.updateUserSettings(username, dtoMapper.fromDto(request));
            return ResponseEntity.ok().build();
        });
    }

}
