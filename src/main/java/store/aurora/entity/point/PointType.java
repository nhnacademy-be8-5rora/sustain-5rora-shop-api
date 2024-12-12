package store.aurora.entity.point;

public enum PointType {
    EARNED("적립"),
    USED("사용"),
    EARNED_CANCEL("적립취소"),
    USED_CANCEL("사용취소");


    private final String korean;

    PointType(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    // 한국어 값으로 PointType 찾기
    public static PointType fromKorean(String korean) {
        for (PointType type : PointType.values()) {
            if (type.getKorean().equalsIgnoreCase(korean)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Korean value: " + korean);
    }
}
