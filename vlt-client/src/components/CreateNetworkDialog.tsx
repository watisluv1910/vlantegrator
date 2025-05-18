// src/components/NetworkDialog.tsx
import React, {useState, useEffect} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from "@mui/material";

export interface NetworkDialogProps {
    open: boolean;
    onClose: () => void;
    onCreate: (name: string, driver: string) => Promise<void>;
    /**
     * Optional: initial driver value (e.g. “bridge”)
     */
    defaultDriver?: string;
}

const ALL_DRIVERS: string[] = [
    "bridge",
    "host",
    "overlay",
    "macvlan",
    "none",
];

export const NetworkDialog: React.FC<NetworkDialogProps> = ({
                                                                open,
                                                                onClose,
                                                                onCreate,
                                                                defaultDriver = "bridge",
                                                            }) => {
    const [name, setName] = useState("");
    const [driver, setDriver] = useState<string>(defaultDriver);
    const [isSubmitting, setSubmitting] = useState(false);

    useEffect(() => {
        if (open) {
            setName("");
            setDriver(defaultDriver);
            setSubmitting(false);
        }
    }, [open, defaultDriver]);

    const handleConfirm = async () => {
        setSubmitting(true);
        try {
            await onCreate(name.trim(), driver);
            onClose();
        } catch (e) {
            console.error(e);
            setSubmitting(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>Создание новой Docker-сети</DialogTitle>
            <DialogContent>
                <DialogContentText sx={{mb: 2}}>
                    Укажите имя и выберите драйвер сети.
                </DialogContentText>

                <TextField
                    label="Имя сети"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    fullWidth
                    margin="dense"
                    autoFocus
                />

                <FormControl fullWidth margin="dense">
                    <InputLabel id="driver-select-label">Драйвер</InputLabel>
                    <Select
                        labelId="driver-select-label"
                        value={driver}
                        label="Драйвер"
                        onChange={(e) => setDriver(e.target.value as string)}
                    >
                        {ALL_DRIVERS.map((drv) => (
                            <MenuItem key={drv} value={drv}>
                                {drv}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={isSubmitting}>
                    Отмена
                </Button>
                <Button
                    variant="contained"
                    onClick={handleConfirm}
                    disabled={!name.trim() || isSubmitting}
                >
                    {isSubmitting ? "Создание…" : "Создать"}
                </Button>
            </DialogActions>
        </Dialog>
    );
};