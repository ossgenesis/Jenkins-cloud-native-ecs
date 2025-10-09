// Get current date and time
def currentDate = new Date()
def formattedDate = currentDate.format('yyyy-MM-dd HH:mm:ss')
echo "The current date and time is ${formattedDate}"
