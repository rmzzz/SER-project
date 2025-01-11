package bong.addressparser;

import bong.canvas.CanvasElement;
import bong.canvas.Range;
import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Address extends CanvasElement implements Serializable, Comparable<Address> {
    private static final long serialVersionUID = 1L;

    private final String street, house, postcode, city, municipality;

    private final float lat, lon;
    private Range boundingBox;

    public Address(
            String street,
            String house,
            String postcode,
            String city,
            String municipality,
            float lat,
            float lon
    ) {
        this.street = internNonNull(street);
        this.house = internNonNull(house);
        this.postcode = internNonNull(postcode);
        this.city = internNonNull(city);
        this.municipality = internNonNull(municipality);
        this.lat = lat;
        this.lon = lon;

        setBoundingBox();
    }

    static String internNonNull(String s) {
        return s != null ? s.intern() : null;
    }

    public String toString() {
        return (
                (street != null ? street + " " : "") +
                        (house != null ? house + ", " : "") +
                        (postcode != null ? postcode + " " : "") +
                        city
        );
    }

    static String regex =
    "^ *(?<street>(?:\\d+\\. ?)?[a-zæøåÆØÅé\\-\\. ]+(?<! ))(?: (?<house>[\\da-z]+(?:\\-\\d)?)?)?,?(?: (?<floor>(?:st)|(?:\\d{1,2}(?!\\d)))?(?:\\.|,| )? ?)?(?:(?<side>(?:tv|th|mf)|(?:\\d{1,3}))\\.?)?(?:[\\.|,| ])*(?<postcode>\\d{4})? ?(?<city>[a-zæøåÆØÅ\\-\\.]+[a-zæøåÆØÅ\\-\\. ]*?[a-zæøåÆØÅ\\-\\.]*)? *$";
    static Pattern pattern = Pattern.compile(
            regex,
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    public static Address parse(String input) throws InvalidAddressException {
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return new Builder()
                    .street(matcher.group("street"))
                    .house(matcher.group("house"))
                    .postcode(matcher.group("postcode"))
                    .city(matcher.group("city"))
                    .build();
        } else {
            throw new InvalidAddressException(input);
        }
    }

    @Override
    public int compareTo(Address that) {
        // street house (floor side postcode) city
        int cmp;
        if ((cmp = compareFields(this.street, that.street)) != 0) {
            return cmp;
        }
        if ((cmp = compareFields(this.house, that.house)) != 0) {
            return cmp;
        }
        return compareFields(this.city, that.city);
    }

    static int compareFields(String a, String b) {
        return (a == null && b == null) ? 0
                : (a == null) ? -1
                        : (b == null) ? 1
                                : a.compareTo(b);
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public String getMunicipality() {
        return municipality;
    }

    @Override
    public Point2D getCentroid() {
        return new Point2D(this.lon, this.lat);
    }

    @Override
    public Range getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox() {
        this.boundingBox = new Range(this.lon, this.lat, this.lon, this.lat);
    }

    public static class Builder {
        private String street, house, postcode, city, municipality;
        private float lat, lon;
        private boolean isEmpty = true;

        public boolean isEmpty() {
            return isEmpty;
        }

        public Builder street(String _street) {
            street = _street;
            isEmpty = false;
            return this;
        }

        public Builder house(String _house) {
            house = _house;
            return this;
        }

        public Builder floor(String _floor) {
            return this;
        }

        public Builder side(String _side) {
            return this;
        }

        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        public Builder city(String _city) {
            city = _city;
            return this;
        }

        public Builder municipality(String _municipality) {
            municipality = _municipality;
            return this;
        }

        public Builder lat(float _lat) {
            lat = _lat;
            return this;
        }

        public Builder lon(float _lon) {
            lon = _lon;
            return this;
        }

        public Address build() {
            return new Address(street, house, postcode, city, municipality, lat, lon);
        }
    }
}