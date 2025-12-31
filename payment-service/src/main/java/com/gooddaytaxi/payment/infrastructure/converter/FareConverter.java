package com.gooddaytaxi.payment.infrastructure.converter;

import com.gooddaytaxi.payment.domain.vo.Fare;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FareConverter implements AttributeConverter<Fare,Long> {
    @Override
    public Long convertToDatabaseColumn(Fare fare) {
        if (fare == null) {
            return null;
        }
        return fare.value();  // record라면 .value(), 클래스라면 getter
    }

    @Override
    public Fare convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return Fare.of(dbData);    // 여기서 검증 포함
    }
}
