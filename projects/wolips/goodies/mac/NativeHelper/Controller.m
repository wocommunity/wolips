#import "Controller.h"

@implementation Controller

- (void)cdInTerminal:(NSString *)anArgument
{
    NSString *aString = @"tell application \"Terminal\"\n do script \"cd ";
    aString = [aString stringByAppendingString: anArgument];
    aString = [aString stringByAppendingString: @"\"\n activate\nend tell"];
    NSAppleScript *anAppleScript = [[NSAppleScript alloc] initWithSource: aString];
    NSDictionary **errorInfo = NULL;
    NSAppleEventDescriptor *AppleEventDescriptor = [anAppleScript executeAndReturnError: errorInfo];
}

- (void)revealInFinder:(NSString *)anArgument
{
    [[NSWorkspace sharedWorkspace] selectFile: anArgument inFileViewerRootedAtPath: anArgument];
}

- (NSString *)parseArgumentForRevealInFinder:(NSString *)aString
{
    int length = [aString length];
    NSRange range = { 1, length - 2};
    NSMutableString *aMutableString = [[aString substringWithRange: range] mutableCopy];
    [aMutableString replaceOccurrencesOfString:@":" withString: @" " options:NSCaseInsensitiveSearch range: NSMakeRange(0, [aMutableString length])];
    return aMutableString;
}

- (NSString *)parseArgumentForCdInTerminal:(NSString *)aString
{
    int length = [aString length];
    NSRange range = { 1, length - 2};
    NSMutableString *aMutableString = [[aString substringWithRange: range] mutableCopy];
    [aMutableString replaceOccurrencesOfString:@":" withString: @"\\ " options:NSCaseInsensitiveSearch range: NSMakeRange(0, [aMutableString length])];
    return aMutableString;
}

- (void)runAction:(NSString *)anAction withArgument:(NSString *)anArgument
{
    if([@"REVEAL_IN_FINDER" isEqual: anAction]) {
        [self revealInFinder: [self parseArgumentForRevealInFinder: anArgument]];
    }
    if([@"CD_IN_TERMINAL" isEqual: anAction]) {
        [self cdInTerminal: [self parseArgumentForCdInTerminal: anArgument]];
    }
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
[self cdInTerminal: [self parseArgumentForCdInTerminal: @"\"/Volumes/Daten/Projekte/temp_workspaces/runtime-workspace/d/a:d:d\""]];
    NSProcessInfo *processInfo = [NSProcessInfo processInfo];
    NSArray *arguments = [processInfo arguments];
    int count = [arguments count];
    if(count > 1)
    {
        [self runAction: [arguments objectAtIndex: count - 2] withArgument: [arguments objectAtIndex: count - 1]];
    }    
    [[aNotification object] terminate: self];
}

@end
