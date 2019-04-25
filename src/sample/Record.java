package sample;

public class Record {
    private String StuNum, Name, Phone, Email;

    public Record(String stuNum, String name, String phone, String email) {
        StuNum = stuNum;
        Name = name;
        Phone = phone;
        Email = email;
    }

    @Override
    public String toString() {
        return StuNum + '|' + Name + '|' + Phone + '|' + Email + '|' + '\n';
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getName() {
        return Name;
    }

    public String getStuNum() {
        return StuNum;
    }
}
