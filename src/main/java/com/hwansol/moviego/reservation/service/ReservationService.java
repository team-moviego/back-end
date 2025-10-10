
    // 예약 번호 생성 메서드
    // 예: 날짜 + 6자리 난수
    // 20251010854940
    private String createReservationNum() {
        Random random = new Random();
        int randomNum = random.nextInt(1000000);

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int value = now.getMonth().getValue();
        int dayOfMonth = now.getDayOfMonth();

        return year + String.format("%2s", value).replace(" ", "0") + String.format("%2s", dayOfMonth).replace(" ", "0") + randomNum;
    }
}
