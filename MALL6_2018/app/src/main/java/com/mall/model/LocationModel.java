package com.mall.model;

/**
 * Created by Administrator on 2017/12/21.
 */

public class LocationModel {

    private LocationModel() {

    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "locationType=" + locationType +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", accuracy=" + accuracy +
                ", speed=" + speed +
                ", bearing=" + bearing +
                ", satellites=" + satellites +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", district='" + district + '\'' +
                ", adCode='" + adCode + '\'' +
                ", address='" + address + '\'' +
                ", poiName='" + poiName + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    private LocationModel(Builder builder) {
        this.locationType = builder.locationType;
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
        this.accuracy = builder.accuracy;
        this.speed = builder.speed;
        this.satellites = builder.satellites;
        this.country = builder.country;
        this.province = builder.province;
        this.city = builder.city;
        this.cityCode = builder.cityCode;
        this.district = builder.district;
        this.adCode = builder.adCode;
        this.address = builder.address;
        this.poiName = builder.poiName;
        this.time = builder.time;
    }

    public static LocationModel getLocationModel() {
        if (locationModel == null) {
            locationModel = new LocationModel();
        }
        return locationModel;
    }

    public static void setLocationModel(LocationModel builder) {
        locationModel = null;
        locationModel = builder;

    }


    private static LocationModel locationModel;

    private int locationType;
    private double longitude;
    private double latitude;
    private float accuracy;
    private float speed;
    private float bearing;
    private int satellites;
    private String country = "";
    private String province = "";
    private String city = "";
    private String cityCode = "";
    private String district = "";
    private String adCode = "";
    private String address = "";
    private String poiName = "";
    private String time = "";

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }


    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }


    public float getBearing() {
        return bearing;
    }


    public int getSatellites() {
        return satellites;
    }


    public String getCountry() {
        return country;
    }


    public String getProvince() {
        return province;
    }


    public String getCity() {
        return city;
    }


    public String getCityCode() {
        return cityCode;
    }


    public String getDistrict() {
        return district;
    }


    public String getAdCode() {
        return adCode;
    }


    public String getAddress() {
        return address;
    }


    public String getPoiName() {
        return poiName;
    }


    public String getTime() {
        return time;
    }


    public static class Builder {
        private int locationType;
        private double longitude;
        private double latitude;
        private float accuracy;
        private float speed;
        private float bearing;
        private int satellites;
        private String country = "";
        private String province = "";
        private String city = "";
        private String cityCode = "";
        private String district = "";
        private String adCode = "";
        private String address = "";
        private String poiName = "";
        private String time = "";

        public int getLocationType() {
            return locationType;
        }

        public Builder setLocationType(int locationType) {
            this.locationType = locationType;
            return this;
        }

        public double getLongitude() {
            return longitude;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public double getLatitude() {
            return latitude;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public float getAccuracy() {
            return accuracy;
        }

        public Builder setAccuracy(float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public float getSpeed() {
            return speed;
        }

        public Builder setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public float getBearing() {
            return bearing;
        }

        public Builder setBearing(float bearing) {
            this.bearing = bearing;
            return this;
        }

        public int getSatellites() {
            return satellites;

        }

        public Builder setSatellites(int satellites) {
            this.satellites = satellites;
            return this;
        }

        public String getCountry() {
            return country;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public String getProvince() {
            return province;
        }

        public Builder setProvince(String province) {
            this.province = province;
            return this;
        }

        public String getCity() {
            return city;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public String getCityCode() {
            return cityCode;
        }

        public Builder setCityCode(String cityCode) {
            this.cityCode = cityCode;
            return this;
        }

        public String getDistrict() {
            return district;
        }

        public Builder setDistrict(String district) {
            this.district = district;
            return this;
        }

        public String getAdCode() {
            return adCode;
        }

        public Builder setAdCode(String adCode) {
            this.adCode = adCode;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getPoiName() {
            return poiName;
        }

        public Builder setPoiName(String poiName) {
            this.poiName = poiName;
            return this;
        }

        public String getTime() {
            return time;
        }

        public Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public LocationModel build() {
            return new LocationModel(this);
        }

    }


}
