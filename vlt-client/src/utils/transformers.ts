import {
    Save as SaveIcon,
    Handyman as BuildIcon,
    Start as StartIcon,
    StopCircle as StopIcon,
    RestartAlt as RestartIcon,
    DeleteForever as DeleteIcon,
    RemoveCircle as RemoveIcon,
    Rowing as DefaultIcon,
} from "@mui/icons-material";
import {SvgIconTypeMap} from "@mui/material";
import {OverridableComponent} from "@mui/material/OverridableComponent";

const sizes: string[] = ["B", "KB", "MB", "GB", "TB"];

export function formatBytes(bytes: number, decimals: number = 1): string {
    if (bytes === 0) return "0 B";
    const k = 1024;
    const dm = Math.max(0, decimals);
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    const value = parseFloat((bytes / Math.pow(k, i)).toFixed(dm));
    return `${value} ${sizes[i]}`;
}

const units: { unit: Intl.RelativeTimeFormatUnit; ms: number }[] = [
    {unit: "year", ms: 31536000000},
    {unit: "month", ms: 2628000000},
    {unit: "day", ms: 86400000},
    {unit: "hour", ms: 3600000},
    {unit: "minute", ms: 60000},
    {unit: "second", ms: 1000},
];

const rtf = new Intl.RelativeTimeFormat("ru", {numeric: "always"});

/**
 * Get a language-sensitive relative time message from Dates.
 *
 * @param relative  the relative dateTime, generally is in the past or future.
 * @param pivot     the dateTime of reference, generally is the current time.
 */
export function relativeTimeFromDates(relative: Date | null, pivot: Date = new Date()): string {
    if (!relative) return "";
    const elapsed = relative.getTime() - pivot.getTime();
    return relativeTimeFromElapsed(elapsed);
}

/**
 * Get a language-sensitive relative time message from elapsed time.
 *
 * @param elapsed the elapsed time in milliseconds.
 */
export function relativeTimeFromElapsed(elapsed: number): string {
    for (const {unit, ms} of units) {
        if (Math.abs(elapsed) >= ms || unit === "second") {
            return rtf.format(Math.round(elapsed / ms), unit);
        }
    }
    return "";
}

const actionIconMap: Record<string, OverridableComponent<SvgIconTypeMap>> = {
    create: SaveIcon,
    delete: DeleteIcon,
    build: BuildIcon,
    start: StartIcon,
    stop: StopIcon,
    restart: RestartIcon,
    remove: RemoveIcon,
    _default: DefaultIcon,
};

export function iconForAction(action: string): OverridableComponent<SvgIconTypeMap> {
    return actionIconMap[action] || actionIconMap._default;
}