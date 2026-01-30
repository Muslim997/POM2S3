'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { supabase } from '@/lib/supabase';
import { FileText, Download, Calendar, MessageSquare, Upload, Plus } from 'lucide-react';
import Button from '@/components/Button';
import Link from 'next/link';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export default function CourseDetailPage() {
  const router = useRouter();
  const params = useParams();
  const { user } = useAuthStore();
  const [course, setCourse] = useState<any>(null);
  const [materials, setMaterials] = useState<any[]>([]);
  const [assignments, setAssignments] = useState<any[]>([]);
  const [announcements, setAnnouncements] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('materials');

  const loadCourseData = useCallback(async () => {
    if (!user || !params.id) return;

    try {
      const { data: courseData, error: courseError } = await supabase
        .from('subjects')
        .select('*, profiles(full_name)')
        .eq('id', params.id)
        .single();

      if (courseError) throw courseError;
      setCourse(courseData);

      const { data: materialsData } = await supabase
        .from('course_materials')
        .select('*')
        .eq('subject_id', params.id)
        .order('created_at', { ascending: false });

      setMaterials(materialsData || []);

      const { data: assignmentsData } = await supabase
        .from('assignments')
        .select('*')
        .eq('subject_id', params.id)
        .order('due_date', { ascending: true });

      setAssignments(assignmentsData || []);

      const { data: announcementsData } = await supabase
        .from('announcements')
        .select('*, profiles(full_name)')
        .eq('subject_id', params.id)
        .order('created_at', { ascending: false });

      setAnnouncements(announcementsData || []);
    } catch (error) {
      console.error('Error loading course data:', error);
    } finally {
      setLoading(false);
    }
  }, [user, params.id]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadCourseData();
  }, [user, router, params.id, loadCourseData]);

  if (!user || loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </Layout>
    );
  }

  if (!course) {
    return (
      <Layout>
        <Card>
          <CardContent className="text-center py-12">
            <p className="text-gray-600">Cours non trouvé</p>
          </CardContent>
        </Card>
      </Layout>
    );
  }

  const isTeacher = user.role === 'teacher' && course.teacher_id === user.id;
  const isAdmin = user.role === 'admin';
  const canEdit = isTeacher || isAdmin;

  return (
    <Layout>
      <div className="mb-8">
        <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg p-6 text-white mb-6">
          <h1 className="text-3xl font-bold mb-2">{course.title}</h1>
          <p className="text-blue-100 mb-4">{course.code}</p>
          <p className="text-white/90">{course.description}</p>
          <div className="mt-4 flex items-center text-sm">
            <span className="mr-4">Prof. {course.profiles?.full_name}</span>
            <span>{course.credits} crédits</span>
          </div>
        </div>

        <div className="flex space-x-2 mb-6 border-b border-gray-200">
          <button
            onClick={() => setActiveTab('materials')}
            className={`px-4 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'materials'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            Supports de cours
          </button>
          <button
            onClick={() => setActiveTab('assignments')}
            className={`px-4 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'assignments'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            Devoirs
          </button>
          <button
            onClick={() => setActiveTab('announcements')}
            className={`px-4 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'announcements'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            Annonces
          </button>
          <button
            onClick={() => setActiveTab('forum')}
            className={`px-4 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'forum'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            Forum
          </button>
        </div>

        {activeTab === 'materials' && (
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-gray-900">Supports de cours</h2>
              {canEdit && (
                <Button size="sm">
                  <Upload size={16} className="mr-2" />
                  Ajouter un fichier
                </Button>
              )}
            </div>

            {materials.length === 0 ? (
              <Card>
                <CardContent className="text-center py-12">
                  <FileText size={48} className="mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600">Aucun support de cours disponible</p>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-3">
                {materials.map((material) => (
                  <Card key={material.id}>
                    <CardContent className="flex items-center justify-between">
                      <div className="flex items-center space-x-3">
                        <FileText className="text-blue-600" size={24} />
                        <div>
                          <p className="font-medium text-gray-900">{material.title}</p>
                          <p className="text-sm text-gray-600">
                            {material.file_type} • {(material.file_size / 1024 / 1024).toFixed(2)} MB
                          </p>
                        </div>
                      </div>
                      <Button size="sm" variant="secondary">
                        <Download size={16} className="mr-2" />
                        Télécharger
                      </Button>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'assignments' && (
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-gray-900">Devoirs</h2>
              {canEdit && (
                <Link href={`/assignments/new?course=${course.id}`}>
                  <Button size="sm">
                    <Plus size={16} className="mr-2" />
                    Créer un devoir
                  </Button>
                </Link>
              )}
            </div>

            {assignments.length === 0 ? (
              <Card>
                <CardContent className="text-center py-12">
                  <FileText size={48} className="mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600">Aucun devoir disponible</p>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-3">
                {assignments.map((assignment) => (
                  <Card key={assignment.id}>
                    <CardContent>
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <h3 className="font-semibold text-gray-900 mb-2">{assignment.title}</h3>
                          <p className="text-sm text-gray-600 mb-3">{assignment.description}</p>
                          <div className="flex items-center text-sm text-gray-500">
                            <Calendar size={16} className="mr-2" />
                            <span>
                              Date limite: {format(new Date(assignment.due_date), 'PPP', { locale: fr })}
                            </span>
                            <span className="mx-2">•</span>
                            <span>{assignment.max_points} points</span>
                          </div>
                        </div>
                        <Link href={`/assignments/${assignment.id}`}>
                          <Button size="sm" variant="secondary">
                            Voir
                          </Button>
                        </Link>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'announcements' && (
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-gray-900">Annonces</h2>
              {canEdit && (
                <Button size="sm">
                  <Plus size={16} className="mr-2" />
                  Publier une annonce
                </Button>
              )}
            </div>

            {announcements.length === 0 ? (
              <Card>
                <CardContent className="text-center py-12">
                  <MessageSquare size={48} className="mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600">Aucune annonce</p>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-4">
                {announcements.map((announcement) => (
                  <Card key={announcement.id}>
                    <CardContent>
                      {announcement.is_urgent && (
                        <div className="inline-block px-2 py-1 bg-red-100 text-red-700 text-xs font-medium rounded mb-2">
                          URGENT
                        </div>
                      )}
                      <h3 className="font-semibold text-gray-900 mb-2">{announcement.title}</h3>
                      <p className="text-gray-600 mb-3">{announcement.content}</p>
                      <div className="text-sm text-gray-500">
                        Par {announcement.profiles?.full_name} •
                        {format(new Date(announcement.created_at), 'PPP', { locale: fr })}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'forum' && (
          <Card>
            <CardContent className="text-center py-12">
              <MessageSquare size={48} className="mx-auto text-gray-400 mb-4" />
              <p className="text-gray-600">Forum de discussion à venir</p>
            </CardContent>
          </Card>
        )}
      </div>
    </Layout>
  );
}
