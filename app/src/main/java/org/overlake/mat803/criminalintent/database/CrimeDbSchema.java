package org.overlake.mat803.criminalintent.database;

public class CrimeDbSchema {

    public static final class CrimeTable {

        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT_ID = "suspect_id";
        }
    }

    public static final class SuspectTable {

        public static final String NAME = "suspects";

        public static final class Cols {
            public static final String SUSPECT_ID = "suspect_id";
            public static final String DISPLAY_NAME = "name";
            public static final String PHONE = "phone";
        }
    }

}
