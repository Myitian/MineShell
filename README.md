# MineShell
简体中文介绍见[MC百科](https://www.mcmod.cn/class/8929.html)

Execute shell command in Minecraft!

The standard output and standard error of the process opened by the command will show on chat screen.

The `/shell` command needs permission level 4.

## Compatibility
Minecraft 1.20 +

## License
MIT

## Usages
### /shell run
`/shell run` can run an executable file.

### /shell runcmd
`/shell runcmd` is similar to `/shell run`, but with `cmd /C` or `/bin/sh -c` (depending on the platform) added before the command.\
For example, `/shell runcmd example.txt` will open a text file.

### /shell kill
`/shell kill` can kill current process.

### /shell info
`/shell info` can show information of the process.

### /shell isalive
`/shell isalive` can check if the process is alive.

### /shell input
When using `/shell input <type>`, there will be a line in the suggestion area showing the incomplete line.

`/shell input <type>` can write to the standard input of the process opened by the command. For example, `/shell input line help`.

`<type>` can be one of the following values: `char`, `string`, `line` and `newline`.

| Type    | Value                                                                                                                                                                | Example                              | Effect                                                                                  |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|-----------------------------------------------------------------------------------------|
| char    | a character literal surrounded by single quotes (escape characters is allowed)<br>-&nbsp;or&nbsp;-<br>an integer representing the Unicode code point of a character. | `/shell input char '\n'`             | Writes the character to the standard input of the process.                              |
| string  | a string                                                                                                                                                             | `/shell input string example string` | Writes the string to the standard input of the process.                                 |
| line    | a string                                                                                                                                                             | `/shell input line ping 1.1.1.1`     | Writes the string to the standard input of the process, and appends newline characters. |
| newline | -                                                                                                                                                                    | `/shell input newline`               | Writes newline characters to the standard input of the process.                         |

### /shell config
Config operations.
#### /shell config reload
Reload config from file.
#### /shell config save
Save config to file.
#### /shell config tab-width \<value\>
Set tab width.
#### /shell config input-charset \<value\>
Set input charset, for example, `UTF-8`.
#### /shell config output-charset \<value\>
Set output charset, for example, `UTF-8`.
#### /shell config ansi-escape \<isEnabled\>
Enable/disable ANSI escape codes.

### /shell help
Show help. (WIP)