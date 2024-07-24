package com.mballem.demo_park_api.web.controller;

import com.mballem.demo_park_api.entity.ClienteVaga;
import com.mballem.demo_park_api.service.ClienteVagaService;
import com.mballem.demo_park_api.service.EstacionamentoService;
import com.mballem.demo_park_api.web.dto.EstacionamentoCreateDto;
import com.mballem.demo_park_api.web.dto.EstacionamentoResponseDto;
import com.mballem.demo_park_api.web.dto.mapper.ClienteVagaMapper;
import com.mballem.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Estacionamentos", description = "Operações de registro de entrada e saída de um veículo do estacionamento")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;


    @Operation(
            summary = "Operação de check-in",
            description = "Recurso para dar entrada de um veículo no estacionamento. Requisição exige um Bearer " +
                    "Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),

                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de USER",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(responseCode = "404", description = "Causas possíveis: <br/>- CPF do cliente não " +
                            "encontrado no sistema; <br/>- Nenhuma vaga livre localizada",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(responseCode = "422", description = "Recursos não processados por dados de entrada inválidos",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkIn(@RequestBody @Valid EstacionamentoCreateDto dto) {
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(dto);
        estacionamentoService.checkIn(clienteVaga);

        EstacionamentoResponseDto responseDto = ClienteVagaMapper.toDto(clienteVaga);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation(
            summary = "Localizar um veiculo estacionado",
            description = "Recurso para buscar um veiculo estacionado pelo numero do recibo. Requisição exige um Bearer.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do recibo gerado ao fazer check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),

                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @GetMapping("/check-in/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EstacionamentoResponseDto> getByRecibo(@PathVariable String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Operação de check-out",
            description = "Recurso para dar saída de um veículo no estacionamento. Requisição exige um Bearer " +
                    "Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso executado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),

                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de USER",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),

            }
    )
    @PutMapping("/check-out/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkOut(@PathVariable String recibo) {
        ClienteVaga clienteVaga = estacionamentoService.checkOut(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }
}
