export function formatBytes(bytes: number, decimals: number = 1): string {
    if (bytes === 0) return "0 B";
    const k = 1024;
    const dm = Math.max(0, decimals);
    const sizes = ["B", "KB", "MB", "GB", "TB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    const value = parseFloat((bytes / Math.pow(k, i)).toFixed(dm));
    return `${value} ${sizes[i]}`;
}