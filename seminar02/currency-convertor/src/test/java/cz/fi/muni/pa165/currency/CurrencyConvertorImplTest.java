package cz.fi.muni.pa165.currency;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class CurrencyConvertorImplTest {
    
    private static Currency CZK = Currency.getInstance("CZK");
    private static Currency EUR = Currency.getInstance("EUR");
    
    @Mock
    private ExchangeRateTable exchangeRates;
    private CurrencyConvertor convertor;
    private SoftAssertions softly = new SoftAssertions();
    
    @Before
    public void setup() {
        convertor = new CurrencyConvertorImpl(exchangeRates);
    }

    @Test
    public void testConvert() throws ExternalServiceFailureException{
        when(exchangeRates.getExchangeRate(EUR, CZK))
                .thenReturn(new BigDecimal("25.5"));
        
        softly.assertThat(convertor.convert(EUR, CZK, new BigDecimal("10.050")))
                .isEqualTo(new BigDecimal("256.28"));
        softly.assertThat(converter.convert(EUR, CZK, new BigDecimal("10.051")))
                .isEqualTo(new BigDecimal("256.30"));
        softly.assertThat(converter.convert(EUR, CZK, new BigDecimal("10.150")))
                .isEqualTo(new BigDecimal("258.83"));
        softly.assertThat(converter.convert(EUR, CZK, new BigDecimal("10.2149")))
                .isEqualTo(new BigDecimal("260.48"));

        softly.assertAll();
        
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        assertThatThrowBy(()-> convertor.convert(null, CZK, new BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithNullTargetCurrency() throws ExternalServiceFailureException {
        assertThatThrownBy(() -> converter.convert(EUR, null, BigDecimal.ONE))
            .isInstanceOf(IllegalArgumentException.class);        
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        assertThatThrownBy(() -> converter.convert(EUR, CZK, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source currency is null.");
    }

    @Test
    public void testConvertWithUnknownCurrency() {
        when(exchangeRates.getExchangeRate(EUR, CZK))
                .thenReturn(null);

        assertThatThrownBy(() -> converter.convert(EUR, CZK, BigDecimal.ONE))
            .isInstanceOf(UnknownExchangeRateException.class);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException{
        doThrow(ExternalServiceFailureException.class).when(exchangeRates.getExchangeRate(EUR, CZK));
        
        assertThatThrowBy(()-> convertor.convert(null, CZK, new BigDecimal.ONE))
                .isInstanceOf(UnknownExchangeRateException.class);
    }

}
