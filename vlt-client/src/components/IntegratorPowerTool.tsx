import {useEffect, useRef, useState} from "react";
import {Box, Divider, IconButton, useTheme} from "@mui/material";
import {
    Save as SaveIcon,
    Handyman as BuildIcon,
    Start as StartIcon,
    StopCircle as StopIcon,
    RestartAlt as RestartIcon,
    DeleteForever as DeleteIcon,
    Bolt as BoltIcon,
} from "@mui/icons-material";

const CLOSED_SIZE = 48;
const ICONS_COUNT = 5;
const ICON_AREA = ICONS_COUNT * CLOSED_SIZE;

interface IntegratorPowerToolProps {
    /** How many pixels from the right edge of the right-hand sidebar */
    offsetRight: number;
}

export const IntegratorPowerTool = ({offsetRight}: IntegratorPowerToolProps) => {
    const [open, setOpen] = useState(false);
    const ref = useRef<HTMLDivElement>(null);
    const theme = useTheme();

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (open && ref.current && !ref.current.contains(e.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [open]);

    const DIVIDER_GAP = 0.5;
    const OPEN_HEIGHT = ICON_AREA + DIVIDER_GAP * 2 + 16;

    return <Box
        ref={ref}
        onClick={() => setOpen(true)}
        sx={{
            position: "fixed",
            bottom: 16,
            right: offsetRight + 16,
            zIndex: theme.zIndex.tooltip,
            overflow: "hidden",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "background.paper",
            border: 1,
            borderColor: "divider",
            borderRadius: CLOSED_SIZE,
            transition: theme.transitions.create(["height", "right"], {
                duration: theme.transitions.duration.short,
            }),
            width: CLOSED_SIZE,
            height: open ? OPEN_HEIGHT : CLOSED_SIZE,
        }}
    >
        {open ? (
            <Box
                sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center"
                }}
            >
                <IconButton aria-label="Сохранить маршрут">
                    <SaveIcon/>
                </IconButton>

                <Divider variant="middle" flexItem sx={{my: DIVIDER_GAP}}/>

                <IconButton aria-label="Собрать образ">
                    <BuildIcon/>
                </IconButton>
                <IconButton aria-label="Запустить контейнер">
                    <StartIcon/>
                </IconButton>
                <IconButton aria-label="Остановить контейнер">
                    <StopIcon/>
                </IconButton>
                <IconButton aria-label="Перезапустить контейнер">
                    <RestartIcon/>
                </IconButton>
                <IconButton aria-label="Удалить контейнер">
                    <DeleteIcon/>
                </IconButton>
            </Box>
        ) : (
            <IconButton aria-label="Центр управления" disableRipple>
                <BoltIcon name="powerToolIcon" sx={{color: theme.palette.accent.main}}/>
            </IconButton>
        )}
    </Box>;
}