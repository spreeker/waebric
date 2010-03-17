
from waegenerator import WaeGenerator
from xml.etree import ElementTree as ET
import difflib, sys
testdata = file('tests/wae/suite/tests.dat')

output = 'tests/wae/suite/output'

testdata = testdata.readlines()
testdata = [test.split()[0] for test in testdata]

tests_ok = 0
failed = []



for test in testdata:
    source = open('tests/wae/suite/program/%s.wae' % test)
    try:
        waebric = WaeGenerator(source, output)
    except Exception:
        pass
    Fail = False
    #raw = open('%s/%s.raw.html'% (output, test ))
    try:
        tree1 = ET.parse('%s/%s.raw.html'% (output, test ))
        differ = difflib.unified_diff
    except Exception, err:
        print err
        Fail = True

    try:
        #new = open('%s/%s.htm' % (output, test))
        tree2 = ET.parse('%s/%s.htm'% (output, test ))
    except Exception, err:
        print err
        Fail = True
        #no generated file found probably exception.

    dumptree1 = ET.tostring(tree1.getroot())
    dumptree2 = ET.tostring(tree2.getroot())

    #print dumptree1
    #print dumptree2

    diff = differ(#raw.readlines(), new.readlines(),
                    dumptree1.splitlines(), dumptree2.splitlines(),
                        fromfile = "%s.raw.html" % test,
                        tofile = "%s.htm" % test)
    #diff is a generator.
    diff = [d for d in diff]
    if diff or Fail:
        print test, "FAIL",
        if diff:
            sys.stdout.writelines(diff)
        failed.append(test)
    else:
        print test, "OK"
        tests_ok += 1

print "FAILED ", ", ".join(failed)
print "%d Tests Succeeded of %d" % (tests_ok, len(testdata))
