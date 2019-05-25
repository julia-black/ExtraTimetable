# ExtraTimetable
Приложение для генерации расписания занятий для ВУЗов.

## Общее
ExtraTimetable - desktop-приложение, написанное с помощью Kotlin и фреймворка TornadoFX. 
С помощью него мы сможем работать с локальной базой с информацией о группах, преподавателях, учебном плане, првоодимые парах. 
В интерфейсе мы сможем изменять в ней данные. Основная функциональность - составлять расписание, соответсвующее определенным требованиями с помощью генетических алгоритмов.

## База данных
В качестве БД будет использоваться MongoDB, которая будет развернута локально. Для этого в установочник будет вшит файл-установщик сервиса mongoDB.
В БД будет база - timetable, в ней коллекции:
* groups (факультет, номер группы, кол-во человек)
* teachers (id, ФИО, факультет и их занятость в виде списка времени, когда они могут работать)
* lessons (id, название, преподователь, тип пары, нужен ли проектор, нужны ли компьютеры)
* rooms (номер, корпус, вместимость, кол-во компьютеров)
* plans (списки уч. планов со списками пар и их кол-вом для различных групп)
