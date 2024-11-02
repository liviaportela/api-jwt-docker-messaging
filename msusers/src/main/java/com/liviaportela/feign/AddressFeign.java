package com.liviaportela.feign;

import com.liviaportela.web.dto.AddressResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "addressFeign", url = "https://viacep.com.br/ws/")
public interface AddressFeign {

    @GetMapping("{cep}/json/")
    AddressResponseDto searchAddressCep(@PathVariable("cep") String cep);
}
