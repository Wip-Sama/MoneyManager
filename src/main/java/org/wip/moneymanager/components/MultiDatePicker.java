package org.wip.moneymanager.components;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MultiDatePicker {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ObservableSet<LocalDate> selectedDates;
    private final DatePicker datePicker;

    public MultiDatePicker() {
        this.selectedDates = FXCollections.observableSet(new TreeSet<>());
        this.datePicker = new DatePicker();
        setUpDatePicker();
    }

    public MultiDatePicker withRangeSelectionMode() {
        datePicker.setValue(null);
        EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) -> {
            if (clickEvent.getButton() == MouseButton.PRIMARY) {
                LocalDate selectedDate = this.datePicker.getValue();
                if (selectedDate != null) {
                    // Aggiungiamo la data selezionata
                    if (!this.selectedDates.contains(selectedDate)) {
                        this.selectedDates.add(selectedDate);
                        // Se è la prima selezione, aggiungi tutte le date nell'intervallo
                        if (this.selectedDates.size() > 1) {
                            this.selectedDates.addAll(getRangeGaps((LocalDate) this.selectedDates.toArray()[0],
                                    (LocalDate) this.selectedDates.toArray()[this.selectedDates.size() - 1]));
                        }
                    } else {
                        // Se la data è già selezionata, la rimuoviamo
                        this.selectedDates.remove(selectedDate);
                        // Rimuoviamo le date tra la selezione precedente e quella corrente
                        this.selectedDates.removeAll(getTailEndDatesToRemove(this.selectedDates, selectedDate));
                        this.datePicker.setValue(getClosestDateInTree(new TreeSet<>(this.selectedDates), selectedDate));
                    }
                }
            }
            clickEvent.consume();
        };

        this.datePicker.setDayCellFactory((DatePicker param) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Aggiungi o rimuovi il gestore dell'evento click
                if (item != null && !empty) {
                    addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                } else {
                    removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                }

                // Cambia il colore di sfondo delle date selezionate
                if (!selectedDates.isEmpty() && selectedDates.contains(item)) {
                    if (Objects.equals(item, selectedDates.toArray()[0]) || Objects.equals(item, selectedDates.toArray()[selectedDates.size() - 1])) {
                        setStyle("-fx-background-color: -fu-accent;");  // Cambia il colore per il primo e l'ultimo intervallo
                    } else {
                        setStyle("-fx-background-color: -fu-accent");   // Cambia il colore per tutte le altre date
                    }
                } else {
                    setStyle(null);  // Se non è selezionato, resetta lo stile
                }
            }
        });

        return this;
    }

    private void setUpDatePicker() {
        datePicker.setValue(null);
        this.datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date == null) ? "" : DATE_FORMAT.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return ((string == null) || string.isEmpty()) ? null : LocalDate.parse(string, DATE_FORMAT);
            }
        });

        // Bloccare la chiusura automatica del DatePicker
        this.datePicker.setOnMousePressed(event -> event.consume());

        // Gestire il clic sulle date per selezionarle
        EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) -> {
            if (clickEvent.getButton() == MouseButton.PRIMARY) {
                LocalDate selectedDate = this.datePicker.getValue();
                if (selectedDate != null) {
                    if (!this.selectedDates.contains(selectedDate)) {
                        this.selectedDates.add(selectedDate);
                    } else {
                        // Se la data è già selezionata, la rimuoviamo
                        this.selectedDates.remove(selectedDate);
                        this.datePicker.setValue(getClosestDateInTree(new TreeSet<>(this.selectedDates), selectedDate));
                    }
                }
            }
            clickEvent.consume();  // Impedisce che l'evento di clic chiuda il DatePicker
        };

        this.datePicker.setDayCellFactory((DatePicker param) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Aggiungi o rimuovi il gestore dell'evento click per ogni cella
                if (item != null && !empty) {
                    addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                } else {
                    removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                }

                // Modifica lo stile per le date selezionate
                if (selectedDates.contains(item)) {
                    setStyle("-fx-background-color: -fu-accent;");  // Modifica il colore per le date selezionate
                } else {
                    setStyle(null);  // Se non è selezionata, resetta lo stile
                }
            }
        });
    }

    private static Set<LocalDate> getTailEndDatesToRemove(Set<LocalDate> dates, LocalDate date) {
        TreeSet<LocalDate> tempTree = new TreeSet<>(dates);
        tempTree.add(date);

        int higher = tempTree.tailSet(date).size();
        int lower = tempTree.headSet(date).size();

        if (lower <= higher) {
            return tempTree.headSet(date);
        } else if (lower > higher) {
            return tempTree.tailSet(date);
        } else {
            return new TreeSet<>();
        }
    }

    private static LocalDate getClosestDateInTree(TreeSet<LocalDate> dates, LocalDate date) {
        Long lower = null;
        Long higher = null;

        if (dates.isEmpty()) {
            return null;
        }

        if (dates.size() == 1) {
            return dates.first();
        }

        if (dates.lower(date) != null) {
            lower = Math.abs(DAYS.between(date, dates.lower(date)));
        }
        if (dates.higher(date) != null) {
            higher = Math.abs(DAYS.between(date, dates.higher(date)));
        }

        if (lower == null) {
            return dates.higher(date);
        } else if (higher == null) {
            return dates.lower(date);
        } else if (lower <= higher) {
            return dates.lower(date);
        } else if (lower > higher) {
            return dates.higher(date);
        } else {
            return null;
        }
    }

    private static Set<LocalDate> getRangeGaps(LocalDate min, LocalDate max) {
        Set<LocalDate> rangeGaps = new LinkedHashSet<>();
        if (min == null || max == null) {
            return rangeGaps;
        }

        LocalDate lastDate = min.plusDays(1);
        while (lastDate.isAfter(min) && lastDate.isBefore(max)) {
            rangeGaps.add(lastDate);
            lastDate = lastDate.plusDays(1);
        }
        return rangeGaps;
    }

    public ObservableSet<LocalDate> getSelectedDates() {
        return this.selectedDates;
    }

    public DatePicker getDatePicker() {
        return this.datePicker;
    }
}
