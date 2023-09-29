# sas_git

### Команды

* `init` -- инициализация репозитория
* `add <files>` -- добавление файла
* `rm <files>` -- файл удаляется из репозитория, физически остается
* `status` -- измененные/удаленные/не добавленные файлы
* `commit <message>` с проставлением даты и времени
* `reset <to_revision>`. Поведение `reset` совпадает с `git reset --hard`
* `log [from_revision]`

### Запуск тестов

Каждый из тестов выводит лог, полученный после выполнения последовательности команд в текущем TestCase

Запуск: `gradle :test --tests "ru.itmo.mit.git.GitTest"`

### Интерактивный режим

Для того, чтобы опробовать `sas_git` самостоятельно: `INIT_REPO=/path/to/sas_git/repo gradle run --args='command_name'`

`INIT_REPO` --- директория, в которой хотим инициализировать `sas_git` репозиторий.

Пути при `add` и `rm` можно указывать либо абсолютные, либо от директории в INIT_REPO.

Пример: `INIT_REPO=/path/to/sas_git/repo gradle run --args='init'`