# MineShell
Execute shell command in Minecraft!

The standard output and standard error of the process opened by the command will show on chat screen.

The `/shell` command needs permission level 4.

## Compatibility
Minecraft 1.19.3

## License
MIT

## Screenshots and Usages
All screenshots were taken on Windows 10 with Simplified Chinese language.

### /shell runexecutable
`/shell runexecutable` can run an executable file.

#### ping 1.1.1.1
![ping](https://iili.io/Hc6dkqF.png)

![ping result](https://iili.io/Hc6dOs1.png)

#### cmd
![cmd](https://iili.io/Hc6dNWP.png)

### /shell run
![process info](https://iili.io/Hc6diXI.png)
`/shell run` is similar to `/shell runexecutable`, but with `cmd /C` or `/bin/sh -c` (depending on the platform) added before the command. In the example the command opened a text file.

### /shell help
Show help (No effect now, WIP)

### /shell input
![cmd hint](https://iili.io/Hc6dwzB.png)
When using `/shell input <type>`, there will be a line in the suggestion area showing the incomplete line.

![cmd help](https://iili.io/Hc6d8ga.png)
`/shell input <type>` can write to the standard input of the process opened by the command. For example, `/shell input line help`.

`<type>` can be one of the following values: `char`, `string`, `line` and `newline`.

|Type   |Value|Example|Effect|
| ----- | ---  |----  |---  |
|char   |a character literal surrounded by single quotes (escape characters is allowed)<br>-&nbsp;or&nbsp;-<br>an integer representing the Unicode code point of a character.|`/shell input char '\n'`|Writes the character to the standard input of the process.|
|string |a string|`/shell input string example string`|Writes the string to the standard input of the process.|
|line   |a string|`/shell input line ping 1.1.1.1`|Writes the string to the standard input of the process, and appends newline characters.|
|newline| - |`/shell input newline`|Writes newline characters to the standard input of the process.|

### /shell isalive
![is process alive?](https://iili.io/Hc6dUdJ.png)
`/shell isalive` can check if the process is alive.

### /shell info
![process info](https://iili.io/Hc6dg5v.png)
`/shell info` can show information of the process.

### /shell kill
![process info](https://iili.io/Hc6d4bp.png)
`/shell info` can kill current process.