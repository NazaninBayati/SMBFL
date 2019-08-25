cp ${experiment_root}/jtopas/outputs/t$1 ${experiment_root}/jtopas/outputs/tmp
sed '/Time:/d' ${experiment_root}/jtopas/outputs/tmp > ${experiment_root}/jtopas/outputs/t$1
cp ${experiment_root}/jtopas/outputs/t$1 ${experiment_root}/jtopas/outputs/tmp
sed '/OK /d' ${experiment_root}/jtopas/outputs/tmp > ${experiment_root}/jtopas/outputs/t$1
cp ${experiment_root}/jtopas/outputs/t$1 ${experiment_root}/jtopas/outputs/tmp
sed '/Finished after/d' ${experiment_root}/jtopas/outputs/tmp > ${experiment_root}/jtopas/outputs/t$1
rm ${experiment_root}/jtopas/outputs/tmp
