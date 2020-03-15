package bfst.addressparser;

import bfst.OSMReader.Node;

import java.io.Serializable;
import java.util.regex.*;

public class Address implements Serializable, Comparable<Address> {
    public final String street, house, postcode, city, municipality, floor, side;
    public final Node node;

    private Address(
            String _street,
            String _house,
            String _floor,
            String _side,
            String _postcode,
            String _city,
            String _municipality,
            Node _node
    ) {
        street = _street;
        house = _house;
        floor = _floor;
        side = _side;
        postcode = _postcode;
        city = _city;
        municipality = _municipality;
        node = _node;

    }

    public String toString() {
        return (
                (street != null ? street + " " : "") +
                        (house != null ? house + ", " : "") +
                        (postcode != null ? postcode + " " : "") +
                        city
        );
    }

    public String toDetailedString() {
        return (
                "\nStreet: " +
                        street +
                        "\nHouse: " +
                        house +
                        "\nPostcode: " +
                        postcode +
                        "\nCity: " +
                        city
        );
    }
    //TODO municipality in regex
    static String regex =
            "^(?<street>(?:\\d+\\. ?)?[a-zæøåÆØÅé\\-\\. ]+(?<! ))(?: (?<house>[\\da-z]+(?:\\-\\d)?)?)?,?(?: (?<floor>(?:st)|(?:\\d{1}))?(?:\\.|,| )? ?)?(?:(?<side>(?:tv|th|mf)|(?:\\w?\\d{1,3}))\\.?)?,?(?: (?<postcode>\\d{4}) (?<city>[a-zæøåÆØÅ\\-\\. ]+))?$";

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
                    //TODO add municipality functionality
                    //.municipality(matcher.group("municipality"))
                    .build();
        } else {
            throw new InvalidAddressException(input);
        }
    }

    @Override
    public int compareTo(Address that) {
        // street house floor side postcode city municipality

        if(!this
                .street
                .toLowerCase()
                .equals(that
                        .street
                        .toLowerCase())){
            return this.street.toLowerCase().compareTo(that.street.toLowerCase());
        } else if(this.house != null && that.house != null) {
            return this.house.toLowerCase().compareTo(that.house.toLowerCase());
        } else {
            return 0;
        }
    }

    public static class Builder {
        private String street, house, postcode, city, municipality, floor, side;
        private Node node;
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
            floor = _floor;
            return this;
        }

        public Builder side(String _side) {
            side = _side;
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


        public Builder node(Node _node) {
            node = _node;
            return this;
        }

        public Address build() {
            return new Address(street, house, floor, side, postcode, city, municipality, node);
        }
    }
}