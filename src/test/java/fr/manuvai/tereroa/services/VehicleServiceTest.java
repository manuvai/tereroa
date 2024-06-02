package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.mocks.VehicleMock;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.repositories.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class VehicleServiceTest {
    @InjectMocks
    VehicleService vehicleServiceMock;

    @Mock
    VehicleRepository vehicleRepositoryMock;

    @Test
    void testFindAllAvailable_DatesNull_Ok() {

        Mockito.doReturn(Collections.emptyList())
                .when(vehicleRepositoryMock)
                .findAll();

        List<Vehicle> responseList = vehicleServiceMock.findAllAvailable(null, null);

        Assertions.assertTrue(responseList != null && responseList.isEmpty());
    }

    @Test
    void testFindAllAvailable_DatesNotNull_Ok() {
        // GIVEN
        OffsetDateTime[] respositoryDatesMock = {
                OffsetDateTime.parse("2011-12-03T10:15:30+00:00"),
                OffsetDateTime.parse("2011-12-05T10:15:30+00:00"),
                OffsetDateTime.parse("2012-12-03T10:15:30+00:00"),
                OffsetDateTime.parse("2012-12-05T10:15:30+00:00"),
                OffsetDateTime.parse("2013-12-03T10:15:30+00:00"),
                OffsetDateTime.parse("2013-12-05T10:15:30+00:00"),
                };
        List<Vehicle> respositoryResult = VehicleMock.createVehicleList(respositoryDatesMock);
        OffsetDateTime[] expectedDatesMock = {
                OffsetDateTime.parse("2012-12-03T10:15:30+00:00"),
                OffsetDateTime.parse("2012-12-05T10:15:30+00:00"),
                OffsetDateTime.parse("2013-12-03T10:15:30+00:00"),
                OffsetDateTime.parse("2013-12-05T10:15:30+00:00"),
        };
        List<Vehicle> expectedResult = VehicleMock.createVehicleList(expectedDatesMock);

        // WHEN
        Mockito.doReturn(respositoryResult)
                .when(vehicleRepositoryMock)
                .findAll();

        List<Vehicle> responseList = vehicleServiceMock.findAllAvailable(respositoryDatesMock[0], respositoryDatesMock[1]);

        // THEN
        Assertions.assertEquals(expectedResult, responseList);
    }
}
