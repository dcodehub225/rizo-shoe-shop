package com.rizoshoeshop.rizoshoeshop.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(com.rizoshoeshop.rizoshoeshop.entity.Refund.class, com.rizoshoeshop.rizoshoeshop.dto.sale.RefundDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(com.rizoshoeshop.rizoshoeshop.dto.sale.RefundDTO::setProcessedByEmployeeName);
                });

        modelMapper.createTypeMap(com.rizoshoeshop.rizoshoeshop.entity.SaleItem.class, com.rizoshoeshop.rizoshoeshop.dto.sale.SaleItemDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(com.rizoshoeshop.rizoshoeshop.dto.sale.SaleItemDTO::setProductId);
                    mapper.skip(com.rizoshoeshop.rizoshoeshop.dto.sale.SaleItemDTO::setProductName);
                });

        return modelMapper;
    }
}