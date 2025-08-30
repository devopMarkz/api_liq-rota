package com.github.devopMarkz.api_liq_rota.api.dto.frete;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class FreteLoteRequest {

    @NotEmpty
    @Valid
    private List<FreteRequest> viagens;
}
