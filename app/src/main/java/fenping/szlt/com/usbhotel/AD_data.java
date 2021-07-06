package fenping.szlt.com.usbhotel;

import java.util.List;

public class AD_data {


    private List<AdBean> ad;

    public List<AdBean> getAd() {
        return ad;
    }

    public void setAd(List<AdBean> ad) {
        this.ad = ad;
    }

    public static class AdBean {
        /**
         * size :
         * starttime :
         * stoptime :
         * type :
         * url :
         */

        private String size;
        private String starttime;
        private String stoptime;
        private String type;
        private String url;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getStoptime() {
            return stoptime;
        }

        public void setStoptime(String stoptime) {
            this.stoptime = stoptime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "AdBean{" +
                    "size='" + size + '\'' +
                    ", starttime='" + starttime + '\'' +
                    ", stoptime='" + stoptime + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
