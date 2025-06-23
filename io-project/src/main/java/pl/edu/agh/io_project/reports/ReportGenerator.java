package pl.edu.agh.io_project.reports;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.agh.io_project.stats.StatsService;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;
import pl.edu.agh.io_project.storage.Storage;
import pl.edu.agh.io_project.storage.InMemoryMultipartFile;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskService;
import pl.edu.agh.io_project.teams.Team;
import pl.edu.agh.io_project.teams.TeamService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@AllArgsConstructor
@Component
public class ReportGenerator {

    private final ReportResultRepository reportResultRepository;
    private final Storage storage;

    private final StatsService statsService;
    private final TeamService teamService;
    private final TaskService taskService;

    @Async
    @Transactional
    public void generateReportAsync(Long resultId) {
        ReportResult result = reportResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));

        try {
            result.setStatus(ReportStatus.IN_PROGRESS);
            reportResultRepository.save(result);

            Report report = result.getReport();
            byte[] pdfBytes = generatePdf(report);

            String uniqueName = storage.generateUniqueFileName("report_" + resultId + ".pdf");
            String filePath = "reports/" + uniqueName;
            storage.store(new InMemoryMultipartFile(uniqueName, pdfBytes), filePath);

            result.setFilePath(filePath);
            result.setSuccess(true);
            result.setStatus(ReportStatus.COMPLETED);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setStatus(ReportStatus.FAILED);
            result.setDetails("Error: " + e.getMessage());
        }

        reportResultRepository.save(result);
    }

    private byte[] generatePdf(Report report) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph("Report: " + report.getTitle(), titleFont));
        document.add(new Paragraph("Description: " + report.getDescription(), normalFont));
        document.add(new Paragraph("Created by: " + report.getCreatedByUserId(), normalFont));
        document.add(new Paragraph("Created at: " + report.getCreatedDate(), normalFont));
        document.add(new Paragraph("Type: " + report.getReportType(), normalFont));
        document.add(Chunk.NEWLINE);

        switch (report.getReportType()) {
            case TASK_REPORT -> {
                document.add(new Paragraph("Tasks for user: " + report.getCreatedByUserId(), headerFont));
                document.add(Chunk.NEWLINE);

                List<Task> tasks = taskService.getTasksByUserId(report.getCreatedByUserId());
                if (tasks.isEmpty()) {
                    document.add(new Paragraph("No tasks found.", normalFont));
                } else {
                    for (Task task : tasks) {
                        document.add(new Paragraph("• Title: " + task.getTitle(), normalFont));
                        document.add(new Paragraph("  Description: " + task.getDescription(), normalFont));
                        document.add(new Paragraph("  Status: " + task.getStatus(), normalFont));
                        document.add(new Paragraph("  Column: " + (task.getColumn() != null ? task.getColumn().getName() : "N/A"), normalFont));
                        document.add(new Paragraph("  Created at: " + task.getCreatedDate(), normalFont));
                        document.add(Chunk.NEWLINE);
                    }
                }
            }

            case USER_REPORT -> {
                document.add(new Paragraph("User statistics for: " + report.getCreatedByUserId(), headerFont));
                document.add(Chunk.NEWLINE);

                List<UserStatsEntity> statsList = statsService.getUserStatsByUserId(report.getCreatedByUserId());
                if (statsList.isEmpty()) {
                    document.add(new Paragraph("No statistics found.", normalFont));
                } else {
                    for (UserStatsEntity stats : statsList) {
                        document.add(new Paragraph("Stats Record (ID: " + stats.getId() + ")", headerFont));
                        document.add(new Paragraph("Assignments: " + stats.getAssignments(), normalFont));
                        document.add(new Paragraph("Closed: " + stats.getClosed(), normalFont));
                        document.add(new Paragraph("Last Active: " + stats.getLastActive(), normalFont));
                        document.add(new Paragraph("Average Close Time (s): " + stats.getAvgCloseTimeSeconds(), normalFont));

                        document.add(new Paragraph("Status Counts:", normalFont));
                        if (stats.getStatusCounts() != null && !stats.getStatusCounts().isEmpty()) {
                            for (var entry : stats.getStatusCounts().entrySet()) {
                                document.add(new Paragraph("  - " + entry.getKey() + ": " + entry.getValue(), normalFont));
                            }
                        } else {
                            document.add(new Paragraph("  No status data.", normalFont));
                        }

                        document.add(Chunk.NEWLINE);
                    }
                }
            }

            case TEAM_REPORT -> {
                document.add(new Paragraph("Team statistics for user: " + report.getCreatedByUserId(), headerFont));
                document.add(Chunk.NEWLINE);

                List<Team> teams = teamService.getTeamsByUserId(report.getCreatedByUserId());

                if (teams.isEmpty()) {
                    document.add(new Paragraph("No teams found for user.", normalFont));
                } else {
                    for (Team team : teams) {
                        statsService.getTeamStatsByTeamId(String.valueOf(team.getId())).ifPresentOrElse(teamStats -> {
                            document.add(new Paragraph("Team ID: " + teamStats.getTeamId(), headerFont));
                            document.add(new Paragraph("Project ID: " + teamStats.getProjectId(), normalFont));
                            document.add(new Paragraph("Updated at: " + teamStats.getUpdatedAt(), normalFont));
                            document.add(new Paragraph("Created: " + teamStats.getCreatedCount(), normalFont));
                            document.add(new Paragraph("Assigned: " + teamStats.getAssignedCount(), normalFont));
                            document.add(new Paragraph("Closed: " + teamStats.getClosedCount(), normalFont));
                            document.add(new Paragraph("Moved: " + teamStats.getMovedCount(), normalFont));
                            document.add(new Paragraph("Average Close Time (s): " + teamStats.getAvgCloseTimeSeconds(), normalFont));
                            document.add(Chunk.NEWLINE);

                            document.add(new Paragraph("Active Users:", normalFont));
                            if (teamStats.getActiveUsers() != null && !teamStats.getActiveUsers().isEmpty()) {
                                for (String user : teamStats.getActiveUsers()) {
                                    document.add(new Paragraph("  - " + user, normalFont));
                                }
                            } else {
                                document.add(new Paragraph("  No active users.", normalFont));
                            }

                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph("Daily Task Income:", normalFont));
                            if (teamStats.getDailyTaskIncome() != null && !teamStats.getDailyTaskIncome().isEmpty()) {
                                teamStats.getDailyTaskIncome().forEach((day, count) ->
                                        document.add(new Paragraph("  " + day + ": " + count, normalFont))
                                );
                            } else {
                                document.add(new Paragraph("  No daily task income data.", normalFont));
                            }

                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph("Status Counts:", normalFont));
                            if (teamStats.getStatusCounts() != null && !teamStats.getStatusCounts().isEmpty()) {
                                teamStats.getStatusCounts().forEach((status, count) ->
                                        document.add(new Paragraph("  " + status + ": " + count, normalFont))
                                );
                            } else {
                                document.add(new Paragraph("  No status count data.", normalFont));
                            }

                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph("Team Members:", normalFont));
                            if (teamStats.getMembers() != null && !teamStats.getMembers().isEmpty()) {
                                for (String member : teamStats.getMembers()) {
                                    document.add(new Paragraph("  - " + member, normalFont));
                                }
                            } else {
                                document.add(new Paragraph("  No team members.", normalFont));
                            }

                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph("────────────────────────────────────────────", normalFont));
                            document.add(Chunk.NEWLINE);

                        }, () -> {
                            document.add(new Paragraph("No stats found for team: " + team.getId(), normalFont));
                            document.add(Chunk.NEWLINE);
                        });
                    }
                }
            }
        }

        document.close();
        return baos.toByteArray();
    }
}
