package ru.netology.web;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class CardDeliveryTest {
    public String formDate (int addDays) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }
//Тесты на user path
    @Test
    void shouldTestAcceptCorrectData() {
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Ростов-на-Дону");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("Камелия Исакова-Суходольская");
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        SelenideElement successNotification = $("[data-test-id=success-notification]");
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
        successNotification.$(".notification__title").shouldHave(text("Успешно"));
        successNotification.$(".notification__content").shouldHave(text("Встреча успешно запланирована на " + appointmentDay));
    }
    
    @Test
    void shouldRearrangeMeeting() throws InterruptedException {
        String appointmentDay = formDate (7);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Ростов-на-Дону");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("Камелия Исакова-Суходольская");
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        appointmentDay = formDate (11);
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$(new ByText("Запланировать")).click();

        SelenideElement replanNotification = $("[data-test-id=replan-notification]");
        replanNotification.shouldBe(visible, Duration.ofSeconds(15));
        replanNotification.$(".notification__title").shouldHave(text("Необходимо подтверждение"));
        replanNotification.$(".notification__content").shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        replanNotification.$(".notification__content .button__text").shouldHave(text("Перепланировать"));
    }

    //Тесты на некорректные значения полей
    @Test
    void shouldNotAcceptIncorrectTowns() {//Негативный тест на город
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Лазаревское");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("Попов Игорь");
        form.$("[data-test-id=phone] input").setValue("+70000000000");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        $(new ByText("Запланировать")).click();

        form.$("[data-test-id=city] .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotAcceptIncorrectDate() {//тест на Дату
        String appointmentDay = formDate (2);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Якутск");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("Анна-Мария");
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=date] .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }
    @Test
    void shouldNotAcceptIncorrectNames(){//тест на Фамилию и Имя
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Якутск");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("О4ume1ые-ручки");
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=name] .input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

   /* @Test
    void shouldNotAcceptIncorrectPhone(){//тест на телефон
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("О");
        form.$("[data-test-id=phone] input").setValue("+1234567890");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=phone] .input__sub").shouldHave(exactText("Номер указан неверно. Укажите телефон в международном формате: он содержит 11 цифр"));
    }*/

    @Test
    void shouldNotProceedWithUncheckedBox(){//Тест на отжатую галочку согласия с условиями
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("О");
        form.$("[data-test-id=phone] input").setValue("+1234567890");
        //form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=agreement]").shouldHave(cssClass("input_invalid"));
    }

    //Тесты на Пустые поля

    @Test
    void shouldNotTestAcceptEmptyTown() {//город
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("Попов Игорь");
        form.$("[data-test-id=phone] input").setValue("+70000000000");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        $(new ByText("Запланировать")).click();

        form.$("[data-test-id=city] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotAcceptEmptyDate() {//на Дату
        String appointmentDay = formDate (2);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Якутск");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE);
        form.$("[data-test-id=name] input").setValue("Анна-Мария");
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=date] .input__sub").shouldHave(exactText("Неверно введена дата"));
    }
    @Test
    void shouldNotAcceptEmptyName(){//на Фамилию и Имя
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Якутск");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=phone] input").setValue("+73214569870");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=name] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotAcceptEmptyPhone(){//на телефон
        String appointmentDay = formDate (3);

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=date] input").doubleClick().press(Keys.BACK_SPACE).setValue(appointmentDay);
        form.$("[data-test-id=name] input").setValue("О");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$(new ByText("Запланировать")).click();

        form.$("[data-test-id=phone] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }
}
