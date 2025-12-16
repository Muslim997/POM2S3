'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { supabase } from '@/lib/supabase';
import { TrendingUp, Users, BookOpen, FileText, Award } from 'lucide-react';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

type TooltipProps = any;
type LegendProps = any;

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

export default function AnalyticsPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [stats, setStats] = useState<any>({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    if (user.role !== 'admin') {
      router.push('/dashboard');
      return;
    }

    loadAnalytics();
  }, [user, router]);

  const loadAnalytics = async () => {
    try {
      const { count: usersCount } = await supabase
        .from('profiles')
        .select('*', { count: 'exact', head: true });

      const { count: coursesCount } = await supabase
        .from('subjects')
        .select('*', { count: 'exact', head: true });

      const { count: assignmentsCount } = await supabase
        .from('assignments')
        .select('*', { count: 'exact', head: true });

      const { count: submissionsCount } = await supabase
        .from('submissions')
        .select('*', { count: 'exact', head: true });

      const { count: studentsCount } = await supabase
        .from('profiles')
        .select('*', { count: 'exact', head: true })
        .eq('role', 'student');

      const { count: teachersCount } = await supabase
        .from('profiles')
        .select('*', { count: 'exact', head: true })
        .eq('role', 'teacher');

      setStats({
        users: usersCount || 0,
        courses: coursesCount || 0,
        assignments: assignmentsCount || 0,
        submissions: submissionsCount || 0,
        students: studentsCount || 0,
        teachers: teachersCount || 0,
      });
    } catch (error) {
      console.error('Error loading analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!user || user.role !== 'admin') return null;

  const activityData = [
    { name: 'Lun', students: 45, teachers: 12 },
    { name: 'Mar', students: 52, teachers: 15 },
    { name: 'Mer', students: 48, teachers: 11 },
    { name: 'Jeu', students: 61, teachers: 18 },
    { name: 'Ven', students: 55, teachers: 14 },
    { name: 'Sam', students: 20, teachers: 5 },
    { name: 'Dim', students: 15, teachers: 3 },
  ];

  const courseEnrollmentData = [
    { name: 'Algorithmes', students: 45 },
    { name: 'Base de données', students: 52 },
    { name: 'Web Avancé', students: 48 },
    { name: 'Mobile', students: 38 },
    { name: 'IA', students: 55 },
  ];

  const submissionStatusData = [
    { name: 'À faire', value: 35 },
    { name: 'Soumis', value: 45 },
    { name: 'Notés', value: 120 },
  ];

  const gradeDistributionData = [
    { grade: '0-5', count: 2 },
    { grade: '6-10', count: 15 },
    { grade: '11-15', count: 45 },
    { grade: '16-20', count: 38 },
  ];

  return (
    <Layout>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Tableau de bord analytique</h1>
        <p className="text-gray-600">Vue d'ensemble des statistiques et performances de la plateforme</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Utilisateurs actifs</p>
              <p className="text-3xl font-bold text-gray-900">{stats.users}</p>
              <p className="text-xs text-green-600 mt-1">+12% ce mois</p>
            </div>
            <Users className="text-blue-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Cours totaux</p>
              <p className="text-3xl font-bold text-gray-900">{stats.courses}</p>
              <p className="text-xs text-green-600 mt-1">+5% ce mois</p>
            </div>
            <BookOpen className="text-green-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Devoirs créés</p>
              <p className="text-3xl font-bold text-gray-900">{stats.assignments}</p>
              <p className="text-xs text-green-600 mt-1">+8% ce mois</p>
            </div>
            <FileText className="text-orange-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Soumissions</p>
              <p className="text-3xl font-bold text-gray-900">{stats.submissions}</p>
              <p className="text-xs text-green-600 mt-1">+15% ce mois</p>
            </div>
            <Award className="text-purple-600" size={40} />
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <Card>
          <CardHeader>
            <CardTitle>Activité hebdomadaire</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={activityData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="students" stroke="#3B82F6" name="Étudiants" strokeWidth={2} />
                <Line type="monotone" dataKey="teachers" stroke="#10B981" name="Enseignants" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Inscriptions par cours</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={courseEnrollmentData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="students" fill="#3B82F6" name="Étudiants inscrits" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <Card>
          <CardHeader>
            <CardTitle>Statut des devoirs</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={submissionStatusData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }: any) => `${name} ${((percent || 0) * 100).toFixed(0)}%`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {submissionStatusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Distribution des notes</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={gradeDistributionData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="grade" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#8B5CF6" name="Nombre d'étudiants" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>KPI - Taux d'assiduité</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center">
              <p className="text-5xl font-bold text-green-600">87%</p>
              <p className="text-gray-600 mt-2">Présence moyenne</p>
              <div className="mt-4 h-2 bg-gray-200 rounded-full overflow-hidden">
                <div className="h-full bg-green-600" style={{ width: '87%' }}></div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Taux de remise des devoirs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center">
              <p className="text-5xl font-bold text-blue-600">92%</p>
              <p className="text-gray-600 mt-2">Devoirs soumis à temps</p>
              <div className="mt-4 h-2 bg-gray-200 rounded-full overflow-hidden">
                <div className="h-full bg-blue-600" style={{ width: '92%' }}></div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Taux de réussite</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center">
              <p className="text-5xl font-bold text-purple-600">78%</p>
              <p className="text-gray-600 mt-2">Notes ≥ 10/20</p>
              <div className="mt-4 h-2 bg-gray-200 rounded-full overflow-hidden">
                <div className="h-full bg-purple-600" style={{ width: '78%' }}></div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
}
