# GanttTUI: TUI-based Gantt Chart

> A personal, lightweight, keyboard-driven Gantt chart for your terminal — built for solo project tracking without the overhead of collaborative tools.

---

## Overview

GanttTUI is a terminal UI Gantt chart built with Maven and Lanterna. Tasks are tied to real calendar dates, progress is tracked in days, and the timeline scales to fill your terminal width. Navigate months, move tasks, adjust durations, and track completion — all from the keyboard.

---

## How It Works

```
Select Task -> Move / Resize -> Track Progress -> Switch Months -> View Timeline
```

1. **Select** — Use `j/k` or arrow keys to move between tasks
2. **Move** — Shift a task forward or backward in time with `h/l`
3. **Resize** — Extend or shrink duration with `+/-`
4. **Progress** — Mark days complete with `>/<`
5. **Navigate** — Switch months with `[/]`; only tasks overlapping that month are shown

---

## Getting Started

### Prerequisites

- Java 25+

### Installation (Fastest Way)

Download the latest `GanttTUI.jar`, `install.sh`, and `uninstall.sh` from the [Releases](https://github.com/kuwarte/gantt-tui/releases) page.

```bash
chmod +x install.sh
./install.sh
```

Then run:

```bash
gtt
```

### Building from Source

```bash
git clone https://github.com/kuwarte/gantt-tui.git
cd gantt-tui
mvn clean package
```

### Uninstall

```bash
./uninstall.sh
```

---

## Keybindings

| Category       | Key(s)        | Action                                |
| :------------- | :------------ | :------------------------------------ |
| **Navigation** | `<j>` / `↓`   | Select next task                      |
|                | `<k>` / `↑`   | Select previous task                  |
|                | `<[` / `]>`   | Change month view                     |
| **Time Mgmt**  | `<h>` / `←`   | Shift task start earlier              |
|                | `<l>` / `→`   | Shift task start later                |
|                | `<+>` / `<=>` | Increase duration (days)              |
|                | `<->`         | Decrease duration (days)              |
| **Progress**   | `<.>` / `<,>` | Advance / Rewind completion           |
| **Management** | `<i>`         | Insert task **Above** selection       |
|                | `<o>`         | Insert task **Below** selection       |
|                | `<r>`         | Rename selected task                  |
|                | `<d>`         | Delete selected task                  |
| **Settings**   | `<t>`         | Cycle Themes (OneDark, Dracula, etc.) |
|                | `<b>`         | Toggle Border weight (Thick/Thin)     |
|                | `<W>`         | Change Workspace Name (Branding)      |
| **System**     | `<?>`         | Cycle Help messages                   |
|                | `<q>`         | **Save all changes and Exit**         |

---

## Layout

```
┌────────────┬──────────────────────────────────────┬───────────────┐
│ TASK       │ 1────5────10────15────20────25────28 │ February 2026 │
├────────────┼──────────────────────────────────────┼───────────────┤
│ Design UI  │ ██████░░░░                           │  2/1-2/6      │
│ Backend    │     ████████░░░░                     │  2/4-2/11     │
│ Testing    │             ████                     │  2/9-2/12     │
└────────────┴──────────────────────────────────────┴───────────────┘
 NORMAL  [Gantt Workspace]                      Task 1/5  Feb 2026
```

- **Left panel** — Task names
- **Middle panel** — Scaled timeline; `█` = done, `░` = remaining
- **Right panel** — Month/year header + per-task date ranges

---

## Features

**Real calendar dates** — tasks store actual LocalDate start and end
**Month filtering** — only tasks overlapping the viewed month are shown; tasks spanning months appear clipped in each
**Auto-detects current month on launch** — centers the timeline on the system date immediately
**Insert, rename, delete tasks interactively** — full CRUD support via keyboard-driven prompts
**Persistent UI preferences** — saves themes, borders, and workspace branding to `config.json`

---

## Contributing

GanttTUI is open for contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## License

MIT
