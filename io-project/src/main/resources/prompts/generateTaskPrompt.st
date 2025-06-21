Forget previous tasks you generated and focus on the current task description.
Based on the following task description: {taskDescription}, generate a JSON object representing a task for a Kanban system.
Make the "title" concise and meaningful. Make the "description" detailed and useful. Estimate how many hours this task might take ("estimatedHours") and set an integer priority (1 = high, 2 = medium, 3 = low).
Return ONLY the JSON object, with the following structure:
{{
  "title": "...",
  "description": "...",
  "priority": ...,
  "estimatedHours": ...
}}