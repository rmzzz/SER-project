package bfst.routeFinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StreetTest {

    @Test
    void testStreet() {

        ArrayList<String> tags = new ArrayList<>();
        tags.add("highway");
        tags.add("motorway");
        tags.add("sidewalk");
        tags.add("");
        tags.add("maxspeed");
        tags.add("65");
        tags.add("name");
        tags.add("testroad");

        Street street = new Street(tags, 80);

        Assertions.assertEquals(true, street.isWalking());
        Assertions.assertEquals(false, street.isBicycle());
        Assertions.assertEquals(true, street.isCar());
        Assertions.assertEquals(false, street.isOnewayBicycle());
        Assertions.assertEquals(false, street.isOnewayCar());
        Assertions.assertEquals(65, street.getMaxspeed());
        Assertions.assertEquals(Street.Role.MOTORWAY, Street.Role.MOTORWAY);
        Assertions.assertEquals("testroad", street.getName());

        tags.clear();
        tags.add("highway");
        tags.add("tertiary");
        street = new Street(tags, 80);
        Assertions.assertEquals(80, street.getMaxspeed());

        tags.clear();
        tags.add("foot");
        tags.add("designated");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isWalking());

        tags.clear();
        tags.add("highway");
        tags.add("crossing");
        tags.add("highway");
        tags.add("cycleway");
        tags.add("highway");
        tags.add("motorway_link");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isWalking());
        Assertions.assertEquals(true, street.isBicycle());
        Assertions.assertEquals(true, street.isCar());
        Assertions.assertEquals(Street.Role.MOTORWAY_LINK, street.getRole());

        tags.clear();
        tags.add("highway");
        tags.add("residential");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isCar());
        Assertions.assertEquals(true, street.isBicycle());
        Assertions.assertEquals(true, street.isWalking());

        tags.clear();
        tags.add("highway");
        tags.add("unclassified");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isCar());
        Assertions.assertEquals(true, street.isBicycle());

        tags.clear();
        tags.add("highway");
        tags.add("mini_roundabout");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isOnewayCar());
        Assertions.assertEquals(true, street.isOnewayBicycle());
        Assertions.assertEquals(Street.Role.ROUNDABOUT, street.getRole());

        tags.clear();
        tags.add("highway");
        tags.add("residential");
        tags.add("service");
        tags.add("parking_aisle");
        street = new Street(tags, 50);
        Assertions.assertEquals(false, street.isCar());
        Assertions.assertEquals(false, street.isWalking());
        Assertions.assertEquals(false, street.isBicycle());
    }

}