package com.uplog.uplog.domain.storage.dto;

public class TempResponse {
    private String url;
    private String sig;
    private MeteringInfo MeteringInfo;

    public class MeteringInfo {
        private String BucketName;
        private String BucketType;
        private long BucketCreatedAt;


        public String getBucketName() {
            return BucketName;
        }

        public void setBucketName(String bucketName) {
            BucketName = bucketName;
        }

        public String getBucketType() {
            return BucketType;
        }

        public void setBucketType(String bucketType) {
            BucketType = bucketType;
        }

        public long getBucketCreatedAt() {
            return BucketCreatedAt;
        }

        public void setBucketCreatedAt(long bucketCreatedAt) {
            BucketCreatedAt = bucketCreatedAt;
        }

        @Override
        public String toString() {
            return "MeteringInfo{" +
                    "BucketName='" + BucketName + '\'' +
                    ", BucketType='" + BucketType + '\'' +
                    ", BucketCreatedAt=" + BucketCreatedAt +
                    '}';
        }
    }

}
