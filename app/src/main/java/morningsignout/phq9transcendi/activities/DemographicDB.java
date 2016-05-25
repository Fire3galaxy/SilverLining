package morningsignout.phq9transcendi.activities;

/**
 * Created by Stella on 5/13/2016.
 */
public class DemographicDB {
    int age;
    String ethnicity;
    boolean firstGenerationCollege;
    String school_year;
    String gender;

    public DemographicDB( int _age, String _ethnicity, boolean fgc, String _school_year, String _gender){
        age = _age;
        ethnicity = _ethnicity;
        firstGenerationCollege = fgc;
        school_year = _school_year;
        gender = _gender;
    }

}
